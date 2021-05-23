package controllers

import forms.BlogPostForm
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.{ Action, AnyContent, BaseController, ControllerComponents, Request }
import repositories.BlogRepository
import reactivemongo.api.bson.compat._

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class BlogController @Inject()(
    implicit executionContext: ExecutionContext,
    val blogRepository: BlogRepository,
    val controllerComponents: ControllerComponents
) extends BaseController {

  def getAllBlogs: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    blogRepository.findAll
      .map {
        case Seq() => NotFound("Database is empty!")
        case blogs => Ok(Json.toJson(blogs))
      }
      .recover {
        case e =>
          e.printStackTrace()
          InternalServerError(e.getMessage)
      }
  }

  def createNewBlog: Action[JsValue] = Action.async(controllerComponents.parsers.json) {
    implicit request: Request[JsValue] =>
      {
        BlogPostForm.blogPostForm.bindFromRequest.fold(
          formWithError => {
            Future.successful(
              BadRequest(s"Cannot parse the request body with an error: $formWithError")
            )
          },
          blogPostForm =>
            blogRepository
              .createBlog(blogPostForm)
              .map { result =>
                Created(s"Response with created record number: ${result.n}")
              }
              .recover {
                case e =>
                  e.printStackTrace()
                  InternalServerError(e.getMessage)
              }
        )
      }
  }
}
