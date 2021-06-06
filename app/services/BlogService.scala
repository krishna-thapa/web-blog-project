package services

import forms.BlogPostForm
import models.Blog.laxJsonWriter
import play.api.libs.json.{ JsObject, Json }
import play.api.mvc.Result
import play.api.mvc.Results.{ BadRequest, Created, NotFound, Ok }
import reactivemongo.api.bson.BSONObjectID
import repositories.BlogRepository
import utils.FutureErrorHandler.ErrorRecover

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }

@Singleton
class BlogService @Inject() (implicit
    executionContext: ExecutionContext,
    val blogRepository: BlogRepository
) {

  def allBlogService: Future[Result] = {
    blogRepository.findAll.map {
      case Seq() => NotFound("Database is empty!")
      case blogs =>
        val jsObjects: Seq[JsObject] = blogs.map(blog => laxJsonWriter.writes(blog))
        Ok(Json.toJson(jsObjects))
    }.errorRecover
  }

  def getBlogService(objectId: BSONObjectID): Future[Result] = {
    blogRepository
      .findOne(objectId)
      .map { blog =>
        blog
          .fold(NotFound("Database is empty!"))(b => Ok(Json.toJson(laxJsonWriter.writes(b))))
      }
  }

  def createBlogService(blogPostForm: BlogPostForm): Future[Result] = {
    blogRepository
      .createBlog(blogPostForm)
      .map { result =>
        Created(s"Response with created record number: ${result.n}")
      }
      .errorRecover
  }

  def updateBlogService(
      objectId: BSONObjectID
  )(implicit updatedBlogForm: BlogPostForm): Future[Result] = {
    blogRepository
      .findOne(objectId)
      .flatMap {
        _.fold(Future.successful(NotFound("Database is empty!"))) { currentBlog =>
          blogRepository
            .updateBlog(currentBlog, updatedBlogForm)
            .map { result =>
              Ok(Json.toJson(s"Response with updated record number: ${result.n}"))
            }
        }
      }
  }

  def deleteBlogService(objectId: BSONObjectID): Future[Result] = {
    blogRepository
      .delete(objectId)
      .map { response =>
        if (response.writeErrors.isEmpty)
          Ok(s"Success on deletion of blog with id: ${objectId.stringify}")
        else BadRequest(s"Error on deletion: ${response.writeErrors}")
      }
  }

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
