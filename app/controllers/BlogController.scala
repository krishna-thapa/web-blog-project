package controllers

import models.Blog
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.{ Action, AnyContent, BaseController, ControllerComponents, Request }
import repositories.BlogRepository

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class BlogController @Inject()(
    implicit executionContext: ExecutionContext,
    val blogRepository: BlogRepository,
    val controllerComponents: ControllerComponents
) extends BaseController {

  def getAllBlogs: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    blogRepository.findAll().map { blogs =>
      Ok(Json.toJson(blogs))
    }
  }

  def createNewBlog: Action[JsValue] = Action.async(controllerComponents.parsers.json) {
    implicit request: Request[JsValue] =>
      {
        request.body
          .validate[Blog]
          .fold(
            _ => Future.successful(BadRequest("Cannot parse the request body")),
            blog =>
              blogRepository.create(blog).map { _ =>
                Created(Json.toJson(blog))
              }
          )
      }
  }
}
