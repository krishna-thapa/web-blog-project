package controllers

import forms.BlogPostForm
import play.api.libs.json.JsValue
import play.api.mvc._
import reactivemongo.api.bson.BSONObjectID
import services.BlogService
import utils.FutureErrorHandler.ErrorRecover
import utils.Logging

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }

@Singleton
class BlogController @Inject()(
    implicit executionContext: ExecutionContext,
    val blogService: BlogService,
    val controllerComponents: ControllerComponents
) extends BaseController
    with Logging {

  def getAllBlogs: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    log.info(s"Executing getAllBlogs method")

    blogService.allBlogService.errorRecover
  }

  def getSelectedBlog(id: String): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      log.info(s"Executing getSelectedBlog with the request id: $id")

      BSONObjectID.parse(id) match {
        case Success(objectId) =>
          blogService.getBlogService(objectId).errorRecover
        case Failure(exception) =>
          Future.successful(
            BadRequest(s"Cannot parse the id: $id, error with: ${exception.getMessage}")
          )
      }
  }

  def createNewBlog: Action[JsValue] = Action.async(controllerComponents.parsers.json) {
    implicit request: Request[JsValue] =>
      log.info(s"Executing createNewBlog method")

      BlogPostForm.blogPostForm
        .bindFromRequest()
        .fold(
          formWithError => {
            Future.successful(
              BadRequest(s"Cannot parse the request body with an error: $formWithError")
            )
          },
          blogPostForm => blogService.createBlogService(blogPostForm).errorRecover
        )

  }
}
