package repositories

import models.Blog
import org.joda.time.DateTime
import play.api.Configuration
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.bson.compat._
import reactivemongo.api.commands.WriteResult

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class BlogRepository @Inject()(
    implicit val executionContext: ExecutionContext,
    val reactiveMongoApi: ReactiveMongoApi,
    config: Configuration
) extends CRUDRepository[Blog] {

  override def mongoDBName: String = config.get[String]("mongodb.dbName")
  override def blogsLimit: Int     = config.get[Int]("mongodb.limit")

  def create(request: Blog): Future[WriteResult] = {
    collection.flatMap(
      _.insert(ordered = false)
        .one(request.copy(createdDate = Some(new DateTime()), updatedDate = Some(new DateTime())))
    )
  }
}
