package services

import forms.BlogPostForm
import models.Blog.laxJsonWriter
import play.api.libs.json.{ JsObject, Json }
import play.api.mvc.Result
import play.api.mvc.Results.{ Created, NotFound, Ok }
import reactivemongo.api.bson.BSONObjectID
import repositories.BlogRepository

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class BlogService @Inject()(
    implicit executionContext: ExecutionContext,
    val blogRepository: BlogRepository
) {

  def allBlogService: Future[Result] = {
    blogRepository.findAll.map {
      case Seq() => NotFound("Database is empty!")
      case blogs =>
        val jsObjects: Seq[JsObject] = blogs.map(blog => laxJsonWriter.writes(blog))
        Ok(Json.toJson(jsObjects))
    }
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
  }
}
