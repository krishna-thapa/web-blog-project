package utils

import play.api.mvc.Result
import play.api.mvc.Results.InternalServerError

import scala.concurrent.{ ExecutionContext, Future }

object FutureErrorHandler extends Logging {
  implicit class ErrorRecover(futureResult: Future[Result])(
      implicit executionContext: ExecutionContext
  ) {
    def errorRecover: Future[Result] = {
      futureResult.recover {
        case e =>
          log.error(s"Internal Error on getSelectedBlog method: ${e.getMessage}")
          InternalServerError(e.getMessage)
      }
    }
  }
}
