package controllers

import forms.{ BlogPostForm, UpdateBlogForm }
import play.api.libs.json.JsValue
import play.api.mvc._
import services.BlogService
import utils.Logging

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class BlogController @Inject()(
    implicit executionContext: ExecutionContext,
    val blogService: BlogService,
    val controllerComponents: ControllerComponents
) extends BaseController
    with Logging {

  def getAllBlogs: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    log.info(s"Executing getAllBlogs method")

    blogService.allBlogService
  }

  def getSelectedBlog(id: String): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      log.info(s"Executing getSelectedBlog with the request id: $id")

      blogService.parseBSONObjectId(id, blogService.getBlogService)
  }

  def createNewBlog(): Action[JsValue] = Action.async(controllerComponents.parsers.json) {
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
          blogPostForm => blogService.createBlogService(blogPostForm)
        )

  }

  def updateBlog(): Action[JsValue] = Action.async(controllerComponents.parsers.json) {
    implicit request: Request[JsValue] =>
      log.info(s"Executing updateBlog method")

      UpdateBlogForm.updateBlogPostForm
        .bindFromRequest()
        .fold(
          formWithError => {
            Future.successful(
              BadRequest(s"Cannot parse the request body with an error: $formWithError")
            )
          },
          updateBlogPostForm => {
            implicit val updatedForm: BlogPostForm = updateBlogPostForm.blogPostForm
            blogService.parseBSONObjectId(
              updateBlogPostForm._id,
              blogService.updateBlogService
            )
          }
        )
  }

  def deleteBlog(id: String): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      log.info(s"Executing deleteBlog with the request id: $id")
      blogService.parseBSONObjectId(id, blogService.deleteBlogService)
  }
}
