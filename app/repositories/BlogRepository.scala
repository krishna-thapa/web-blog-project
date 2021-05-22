package repositories

import models.Blog
import play.api.Configuration
import play.modules.reactivemongo.ReactiveMongoApi

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class BlogRepository @Inject()(
    implicit val executionContext: ExecutionContext,
    val reactiveMongoApi: ReactiveMongoApi,
    config: Configuration
) extends CRUDRepository[Blog] {

  override def mongoDBName: String = config.get[String]("mongodb.dbName")
  override def blogsLimit: Int     = config.get[Int]("mongodb.blog.limit")

  override def allBlogs(limit: Int): Future[Seq[Blog]] = ???
}
