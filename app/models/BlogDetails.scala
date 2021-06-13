package models

import play.api.libs.json.{ OWrites, Reads }
import reactivemongo.api.bson._
import reactivemongo.play.json.compat.bson2json.{ fromDocumentWriter, fromReader }

case class BlogDetails(
    blogPictureId: Option[BSONObjectID],
    blog: Blog
)

object BlogDetails {
  implicit val bsonWriter: BSONDocumentWriter[BlogDetails] = Macros.writer[BlogDetails]
  implicit val bsonReader: BSONDocumentReader[BlogDetails] = Macros.reader[BlogDetails]

  // Resolved from bsonWriter
  val laxJsonWriter: OWrites[BlogDetails] = implicitly[OWrites[BlogDetails]]
  val laxJsonReader: Reads[BlogDetails]   = implicitly[Reads[BlogDetails]]
}
