package models

import org.joda.time.DateTime
import play.api.libs.json.{ Format, Json }
import reactivemongo.bson._

case class Blog(
    id: Option[BSONObjectID],
    title: String,
    blogPost: String,
    createdDate: Option[DateTime],
    updatedDate: Option[DateTime]
)

object Blog {
  // Format macro will inspect the Blog case class fields and produce a JSON
  implicit val blogFormat: Format[Blog] = Json.format[Blog]

  // For BSON, however, we’re implementing our custom serializer since it has
  // external types like DateTime, needs serializers as implicit.
  implicit object BlogBSONReader extends BSONDocumentReader[Blog] {
    override def read(bson: BSONDocument): Blog = {
      Blog(
        bson.getAs[BSONObjectID]("id"),
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
        "id"          -> blog.id,
        "title"       -> blog.title,
        "blogPost"    -> blog.blogPost,
        "createdDate" -> blog.createdDate.map(date => BSONDateTime(date.getMillis)),
        "updatedDate" -> blog.updatedDate.map(date => BSONDateTime(date.getMillis))
      )
    }
  }
}
