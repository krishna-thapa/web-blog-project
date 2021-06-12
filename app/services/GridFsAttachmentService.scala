package services

import javax.inject.Inject
import play.api.mvc.Results.Ok
import play.api.mvc.{ MultipartFormData, Result }
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.bson.{ BSONDocument, BSONObjectID, BSONValue }
import reactivemongo.api.gridfs.ReadFile
import repositories.AttachmentRepository
import utils.FutureErrorHandler.ErrorRecover
import utils.Logging

import scala.concurrent.{ ExecutionContext, Future }

class GridFsAttachmentService @Inject() (implicit
    val executionContext: ExecutionContext,
    val reactiveMongoApi: ReactiveMongoApi
) extends AttachmentRepository
    with CommonService
    with Logging {
  /*
    Add a new picture in the GridFS index or update the existing with a new picture
   */
  def addOrReplaceBlogPicture(
      blogId: String,
      file: MultipartFormData.FilePart[ReadFile[BSONValue, BSONDocument]]
  ): Future[Result] = {
    for {
      gfs <- gridFS
      _ <- gfs.update(
        file.ref.id,
        BSONDocument(
          f"$$set" -> BSONDocument("blogId" -> blogId)
        )
      )
    } yield Ok(s"Successfully uploaded the blog picture for the blogId: $blogId")
  }

  /*
    Remove if there is already an existing picture for the requested blog id
    Only one picture at a time can be stored for each blog id
   */
  def removeBlogPicture(blogId: String): Future[Unit] = {
    for {
      gfs         <- gridFS
      blogPicture <- gfs.find(BSONDocument("blogId" -> blogId)).headOption
    } yield blogPicture.fold(
      log.info(s"Picture not found for blog id: $blogId, saving a new picture in the index")
    ) { picture =>
      log.info(s"Existing picture has been removed for blog id: $blogId")
      // Return Unit once the picture is removed from mongoDb
      gfs.remove(picture.id)
    }
  }

  def removeAttachment(id: BSONObjectID): Future[Result] = {
    gridFS.flatMap { gfs =>
      gfs.remove(id).map(_ => Ok("Successfully removed the attached picture")).errorRecover
    }
  }
}