package forms

import play.api.data.{ Form, Mapping }
import play.api.data.Forms.{ mapping, nonEmptyText, text }

case class BlogPostForm(
    title: String,
    blogPost: String
)
object BlogPostForm {

  val blogPostFormMap: Mapping[BlogPostForm] = mapping(
    "title"    -> nonEmptyText.verifying(_.nonEmpty),
    "blogPost" -> text.verifying(_.nonEmpty)
  )(BlogPostForm.apply)(BlogPostForm.unapply)

  val blogPostForm: Form[BlogPostForm] = Form {
    blogPostFormMap
  }
}
