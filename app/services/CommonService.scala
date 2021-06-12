package services

import play.api.mvc.Result
import play.api.mvc.Results.BadRequest
import reactivemongo.api.bson.BSONObjectID
import utils.FutureErrorHandler.ErrorRecover

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }

trait CommonService {

  implicit val executionContext: ExecutionContext

  def parseBSONObjectId(
      id: String,
      callService: BSONObjectID => Future[Result]
  ): Future[Result] = {
    BSONObjectID.parse(id) match {
      case Success(objectId) =>
        callService(objectId).errorRecover
      case Failure(exception) =>
        Future.successful(
          BadRequest(s"Cannot parse the id: $id, error with: ${exception.getMessage}")
        )
    }
  }
}
