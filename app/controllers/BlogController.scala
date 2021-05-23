package controllers

import forms.BlogPostForm
import models.Blog.laxJsonWriter
import play.api.libs.json.{ JsObject, JsValue, Json }
import play.api.mvc._
import reactivemongo.api.bson.BSONObjectID
import reactivemongo.play.json.compat.bson2json.fromDocumentWriter
import reactivemongo.play.json.compat.json2bson.toDocumentWriter
import repositories.BlogRepository
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
      case blogs =>
        val jsObjects: Seq[JsObject] = blogs.map(blog => laxJsonWriter.writes(blog))
        Ok(Json.toJson(jsObjects))
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
              blog
                .fold(NotFound("Database is empty!"))(b => Ok(Json.toJson(laxJsonWriter.writes(b))))
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
