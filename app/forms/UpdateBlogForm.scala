package forms

import play.api.data.Form
import play.api.data.Forms.{ mapping, nonEmptyText }

case class UpdateBlogForm(
    _id: String,
    blogPostForm: BlogPostForm
)

object UpdateBlogForm {

  val updateBlogPostForm: Form[UpdateBlogForm] = Form {
    mapping(
      "_id"          -> nonEmptyText.verifying(_.nonEmpty).verifying(_.length == 24),
      "blogPostForm" -> BlogPostForm.blogPostFormMap
    )(UpdateBlogForm.apply)(UpdateBlogForm.unapply)
  }
}
