package models

import play.api.libs.json.{ OWrites, Reads }
import reactivemongo.api.bson._
import reactivemongo.play.json.compat.bson2json.{ fromDocumentWriter, fromReader }
import reactivemongo.play.json.compat.lax._

case class Blog(
    // id must start with _ for the BSON object Id
    _id: BSONObjectID,
    title: String,
    blogPost: String,
    createdDate: BSONDateTime,
    updatedDate: BSONDateTime
) extends WithDate

object Blog {

  //http://reactivemongo.org/releases/1.0/documentation/json/overview.html

  implicit val bsonWriter: BSONDocumentWriter[Blog] = Macros.writer[Blog]

  // Resolved from bsonWriter
  val laxJsonWriter: OWrites[Blog] = implicitly[OWrites[Blog]]

  implicit val bsonReader: BSONDocumentReader[Blog] = Macros.reader[Blog]

  // resolved from laxBsonReader
  val laxJsonReader: Reads[Blog] = implicitly[Reads[Blog]]
}
