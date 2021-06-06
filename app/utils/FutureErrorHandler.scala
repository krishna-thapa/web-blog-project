package utils

import play.api.mvc.Result
import play.api.mvc.Results.InternalServerError

import scala.concurrent.{ ExecutionContext, Future }

object FutureErrorHandler extends Logging {

  // An implicit class to apply future recover method for any time of Future[Result] response
  implicit class ErrorRecover(futureResult: Future[Result])(implicit
      executionContext: ExecutionContext
  ) {
    def errorRecover: Future[Result] = {
      futureResult.recover { case e =>
        log.error(s"Internal Error on responding future of result method: ${e.getMessage}")
        InternalServerError(e.getMessage)
      }
    }
  }
}
