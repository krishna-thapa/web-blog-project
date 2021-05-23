package models

import org.joda.time.DateTime
import play.api.libs.json.JodaReads._
import play.api.libs.json.JodaWrites._
import play.api.libs.json.{ Json, OFormat }
import reactivemongo.bson.{ BSONObjectID, _ }
import reactivemongo.play.json._

case class Blog(
    // id must start with _ for the BSON object Id
    _id: Option[BSONObjectID],
    title: String,
    blogPost: String,
    createdDate: Option[DateTime],
    updatedDate: Option[DateTime]
)

object Blog {
  // Format macro will inspect the Blog case class fields and produce a JSON
  implicit val blogFormat: OFormat[Blog] = Json.format[Blog]

  // For BSON, however, we’re implementing our custom serializer since it has
  // external types like DateTime, needs serializers as implicit.
  implicit object BlogBSONReader extends BSONDocumentReader[Blog] {
    override def read(bson: BSONDocument): Blog = {
      Blog(
        bson.getAs[BSONObjectID]("_id"),
        bson.getAs[String]("title").get,
        bson.getAs[String]("blogPost").get,
        bson.getAs[BSONDateTime]("createdDate").map(dt => new DateTime(dt.value)),
        bson.getAs[BSONDateTime]("updatedDate").map(dt => new DateTime(dt.value))
      )
    }
  }

  implicit object BlogBSONWriter extends BSONDocumentWriter[Blog] {
    override def write(blog: Blog): BSONDocument = {
      BSONDocument(
        "_id"         -> blog._id,
        "title"       -> blog.title,
        "blogPost"    -> blog.blogPost,
        "createdDate" -> blog.createdDate.map(date => BSONDateTime(date.getMillis)),
        "updatedDate" -> blog.updatedDate.map(date => BSONDateTime(date.getMillis))
      )
    }
  }
}
