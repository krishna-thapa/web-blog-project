package repositories

import models.WithDate
import play.api.Logging
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.{ Cursor, ReadPreference }
import reactivemongo.api.bson.{ BSONDocument, BSONDocumentReader, BSONDocumentWriter }
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.bson.compat._
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.bson.BSONObjectID

import scala.concurrent.{ ExecutionContext, Future }

trait CRUDRepository[T <: WithDate] extends Logging {

  def mongoDBName: String
  def blogsLimit: Int

  implicit def executionContext: ExecutionContext
  def reactiveMongoApi: ReactiveMongoApi

  // The `collection` is a function to avoid potential problems in development with play auto reloading.
  def collection: Future[BSONCollection] = {
    reactiveMongoApi.database.map(_.collection(mongoDBName))
  }

  // Get all the records from collection blogs
  def findAll(
      implicit conWrite: BSONDocumentWriter[T],
      conRead: BSONDocumentReader[T]
  ): Future[Seq[T]] = collection.flatMap(
    _.find(BSONDocument(), Option.empty[T])
      .sort(BSONDocument("createdDate" -> -1))
      //noinspection
      .cursor[T]() //noinspection
      .collect[Seq](blogsLimit, Cursor.FailOnError[Seq[T]]())
  )

  // Get the selected blog
  def findOne(
      id: BSONObjectID
  )(implicit conWrite: BSONDocumentWriter[T], conRead: BSONDocumentReader[T]): Future[Option[T]] = {
    collection.flatMap(_.find(BSONDocument("_id" -> id), Option.empty[T]).one[T])
  }

  // Create a new blog in the collection
  def create(blog: T)(implicit conWrite: BSONDocumentWriter[T]): Future[WriteResult] = {
    collection.flatMap(
      _.insert(ordered = false)
        .one(
          blog
        )
    )
  }
}
