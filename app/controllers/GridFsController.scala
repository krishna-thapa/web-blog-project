package controllers

import javax.inject.{ Inject, Singleton }
import play.api.mvc.{
  AbstractController,
  Action,
  AnyContent,
  ControllerComponents,
  MultipartFormData,
  Result
}
import play.modules.reactivemongo.{ MongoController, ReactiveMongoApi, ReactiveMongoComponents }
import reactivemongo.api.bson.{ BSONDocument, BSONObjectID, BSONValue }
import reactivemongo.api.gridfs.ReadFile
import services.GridFsAttachmentService
import utils.FutureErrorHandler.ErrorRecover
import utils.Logging

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class GridFsController @Inject() (
    components: ControllerComponents,
    implicit val materializer: akka.stream.Materializer,
    implicit val executionContext: ExecutionContext,
    val reactiveMongoApi: ReactiveMongoApi,
    gridFsAttachmentService: GridFsAttachmentService
) extends AbstractController(components)
    with MongoController
    with ReactiveMongoComponents
    with Logging {

  def saveBlogPicture(
      blogId: String
  ): Action[MultipartFormData[ReadFile[BSONValue, BSONDocument]]] =
    Action.async(gridFSBodyParser(gridFsAttachmentService.gridFS)) { request =>
      log.info(s"Executing saveBlogPicture for the request blog id: $blogId")

      val fileOption: Option[MultipartFormData.FilePart[ReadFile[BSONValue, BSONDocument]]] =
        request.body.files.headOption
      fileOption match {
        case Some(file) =>
          log.info(s"Received file: ${file.filename} with content type of: ${file.contentType}")
          gridFsAttachmentService
            .removeBlogPicture(blogId)
            .flatMap(_ => gridFsAttachmentService.addOrReplaceBlogPicture(blogId, file))
            .errorRecover
        case _ => Future.successful(NotFound("Select the picture to upload"))
      }
    }

  // Returns a future Result that serves the first matched file, or a NotFound result.
  def getAttachedPicture(id: String): Action[AnyContent] = Action.async { _ =>
    log.info(s"Executing getAttachedPicture for the request attached file id: $id")
    gridFsAttachmentService.parseBSONObjectId(id, getAttachment)
  }

  // Removes a attachment picture from index store.
  def removeAttachedPicture(id: String): Action[AnyContent] = Action.async { _ =>
    log.info(s"Executing removeAttachedPicture for the request attached file id: $id")
    gridFsAttachmentService.parseBSONObjectId(id, gridFsAttachmentService.removeAttachment)
  }

  private def getAttachment(id: BSONObjectID): Future[Result] = {
    gridFsAttachmentService.gridFS.flatMap { gfs =>
      val attachment = gfs.find(BSONDocument("_id" -> id))
      // Content-Disposition: https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Disposition
      serve(gfs)(attachment, dispositionMode = "inline").errorRecover
    }
  }

}
