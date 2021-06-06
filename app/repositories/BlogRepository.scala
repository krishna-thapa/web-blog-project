package repositories

import forms.BlogPostForm
import models.Blog
import org.joda.time.DateTime
import play.api.Configuration
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.bson.{ BSONDateTime, BSONObjectID }
import reactivemongo.api.commands.WriteResult

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class BlogRepository @Inject() (implicit
    val executionContext: ExecutionContext,
    val reactiveMongoApi: ReactiveMongoApi,
    config: Configuration
) extends CRUDRepository[Blog] {

  override def mongoDBName: String = config.get[String]("mongodb.dbName")
  override def blogsLimit: Int     = config.get[Int]("mongodb.limit")

  val currentDateTimeMilli: BSONDateTime = BSONDateTime(new DateTime().getMillis)

  def createBlog(request: BlogPostForm): Future[WriteResult] = {
    create(
      Blog(
        BSONObjectID.generate(),
        request.title,
        request.blogPost,
        createdDate = currentDateTimeMilli,
        updatedDate = currentDateTimeMilli
      )
    )
  }

  def updateBlog(currentBlog: Blog, blogPostForm: BlogPostForm): Future[WriteResult] = {
    update(
      currentBlog._id,
      currentBlog.copy(
        title = blogPostForm.title,
        blogPost = blogPostForm.blogPost,
        updatedDate = currentDateTimeMilli
      )
    )
  }
}
