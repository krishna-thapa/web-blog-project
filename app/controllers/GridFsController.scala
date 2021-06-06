package controllers

import javax.inject.{ Inject, Singleton }
import play.api.mvc.{ AbstractController, Action, ControllerComponents, MultipartFormData }
import play.modules.reactivemongo.{ MongoController, ReactiveMongoApi, ReactiveMongoComponents }
import reactivemongo.api.bson.{ BSONDocument, BSONObjectID, BSONValue }
import reactivemongo.api.gridfs.ReadFile
import utils.FutureErrorHandler.ErrorRecover
import utils.Logging

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.control.NonFatal

@Singleton
class GridFsController @Inject() (
    components: ControllerComponents,
    implicit val materializer: akka.stream.Materializer,
    val reactiveMongoApi: ReactiveMongoApi
) extends AbstractController(components)
    with MongoController
    with ReactiveMongoComponents
    with Logging {

  implicit def ec: ExecutionContext = components.executionContext

  // a GridFS store named 'attachments'
  private def gridFS: Future[MongoController.GridFS] =
    (for {
      attachments <- reactiveMongoApi.asyncGridFS
      _ <- attachments.ensureIndex().map { index =>
        // let's build an index on our gridfs chunks collection if none
        log.info(s"Checked index, result is $index")
      }
    } yield attachments).recover { case NonFatal(t) =>
      println("Error : " + t)
      throw t
    }

  // gridFSBodyParser from `MongoController`
  // val fsParser: GridFSBodyParser[BSONValue] = gridFSBodyParser(reactiveMongoApi.asyncGridFS)

  def saveBlogPicture(
      blogId: String
  ): Action[MultipartFormData[ReadFile[BSONValue, BSONDocument]]] =
    Action.async(gridFSBodyParser(gridFS)) { request =>
      log.info(s"Executing saveBlogPicture for the request blog id: $blogId")

      val fileOption: Option[MultipartFormData.FilePart[ReadFile[BSONValue, BSONDocument]]] =
        request.body.files.headOption
      fileOption match {
        case Some(file) =>
          log.info(s"File: ${file.filename} with content type of: ${file.contentType}")
          log.info(s"File content type of: ${file.dispositionType}")

          (for {
            gfs       <- gridFS
            blogExist <- gfs.find(BSONDocument("blogId" -> blogId)).headOption
            _         <- gfs.remove(blogExist.get.id)
            _ <- gfs.update(
              file.ref.id,
              BSONDocument(
                f"$$set" -> BSONDocument("blogId" -> blogId)
              )
            )
          } yield Ok("Successfully uploaded the blog picture")).errorRecover
        case _ => Future.successful(NotFound("Select the picture to upload"))
      }
    }
}
