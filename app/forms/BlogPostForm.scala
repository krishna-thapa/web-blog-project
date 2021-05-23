package forms

import play.api.data.Form
import play.api.data.Forms.{ mapping, nonEmptyText, text }

case class BlogPostForm(
    title: String,
    blogPost: String
)
object BlogPostForm {

  val blogPostForm: Form[BlogPostForm] = Form {
    mapping(
      "title"    -> nonEmptyText.verifying(_.nonEmpty),
      "blogPost" -> text
    )(BlogPostForm.apply)(BlogPostForm.unapply)
  }
}
