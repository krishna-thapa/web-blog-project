package repositories

import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.bson.collection.BSONCollection

import scala.concurrent.{ ExecutionContext, Future }

trait CRUDRepository[T] {

  def mongoDBName: String
  def blogsLimit: Int

  implicit def executionContext: ExecutionContext
  def reactiveMongoApi: ReactiveMongoApi

  // The `collection` is a function to avoid potential problems in development with play auto reloading.
  def collection: Future[BSONCollection] = {
    reactiveMongoApi.database.map(_.collection(mongoDBName))
  }

  def allBlogs(limit: Int = blogsLimit): Future[Seq[T]]
}
