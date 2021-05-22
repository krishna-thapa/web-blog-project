package repositories

import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.{ Cursor, ReadPreference }
import reactivemongo.api.bson.{ BSONDocument, BSONDocumentReader, BSONDocumentWriter }
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.bson.compat._

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

  def findAll(
      limit: Int = blogsLimit
  )(implicit conWrite: BSONDocumentWriter[T], conRead: BSONDocumentReader[T]): Future[Seq[T]] = {
    collection.flatMap(
      _.find(BSONDocument(), Option.empty[T])
        .cursor[T](ReadPreference.Primary)
        .collect[Seq](limit, Cursor.FailOnError[Seq[T]]())
    )
  }

}
