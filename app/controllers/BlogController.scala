package controllers

import forms.BlogPostForm
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.{ Action, AnyContent, BaseController, ControllerComponents, Request }
import reactivemongo.api.bson.BSONObjectID
import repositories.BlogRepository
import reactivemongo.api.bson.compat._
import utils.FutureErrorHandler.ErrorRecover
import utils.Logging

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }

@Singleton
class BlogController @Inject()(
    implicit executionContext: ExecutionContext,
    val blogRepository: BlogRepository,
    val controllerComponents: ControllerComponents
) extends BaseController
    with Logging {

  def getAllBlogs: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    log.info(s"Executing getAllBlogs method")

    blogRepository.findAll.map {
      case Seq() => NotFound("Database is empty!")
      case blogs => Ok(Json.toJson(blogs.sortBy(_.createdDate).reverse))
    }.errorRecover
  }

  def getSelectedBlog(id: String): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      log.info(s"Executing getSelectedBlog with the request id: $id")

      BSONObjectID.parse(id) match {
        case Success(objectId) =>
          blogRepository
            .findOne(objectId)
            .map { blog =>
              blog.fold(NotFound("Database is empty!"))(b => Ok(Json.toJson(b)))
            }
            .errorRecover
        case Failure(exception) =>
          Future.successful(
            BadRequest(s"Cannot parse the id: $id, error with: ${exception.getMessage}")
          )
      }
  }

  def createNewBlog: Action[JsValue] = Action.async(controllerComponents.parsers.json) {
    implicit request: Request[JsValue] =>
      log.info(s"Executing createNewBlog method")

      {
        BlogPostForm.blogPostForm
          .bindFromRequest()
          .fold(
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
                .errorRecover
          )
      }
  }
}
