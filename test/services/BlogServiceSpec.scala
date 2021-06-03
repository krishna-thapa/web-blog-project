package services

import com.dimafeng.testcontainers.MongoDBContainer
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import forms.BlogPostForm
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Result
import play.api.test.Helpers.{ contentAsJson, contentAsString, defaultAwaitTimeout, status }

import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, Future }

class BlogServiceSpec extends PlaySpec with TestContainerForAll with GuiceOneAppPerSuite {

  override val containerDef: MongoDBContainer.Def = MongoDBContainer.Def()
  val container: MongoDBContainer                 = startContainers()

  // Create and inject the instance of Application with config needed for MonogoDb
  implicit override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(
      "mongodb.uri"    -> s"${container.replicaSetUrl}",
      "mongodb.dbName" -> "test", // Test Mongo container starts with collection called test
      "mongodb.limit"  -> 20
    )
    .build()

  // Initialize the BlogService class
  val blogService: BlogService = Application.instanceCache[BlogService].apply(app)

  val blogPostForm: BlogPostForm = BlogPostForm("title-1", "description-1")

  "Default Mongo container" must {
    "start in default port" in {
      container.replicaSetUrl must not be empty
    }
  }

  "CRUD operation in mongoDB" must {
    "Should have an empty when initialized" in {
      val result: Future[Result] = blogService.allBlogService
      contentAsString(result) mustBe "Database is empty!"
    }

    "Should insert the blog record in the collection" in {
      val result: Future[Result] = blogService.createBlogService(blogPostForm)
      status(result) mustBe 201
    }

    def id: String = (contentAsJson(blogService.allBlogService) \\ "_id").head.validate[String].get

    "Should get and select the record from the collection" in {
      getBlogTitle(id) mustBe "title-1"
    }

    "Should update the record to the collection" in {
      implicit val updatedForm: BlogPostForm = blogPostForm.copy(title = "title-2")
      Await.ready(blogService.parseBSONObjectId(id, blogService.updateBlogService), Duration.Inf)
      getBlogTitle(id) mustBe "title-2"
    }

    "Should delete the record from the collection" in {
      Await.ready(blogService.parseBSONObjectId(id, blogService.deleteBlogService), Duration.Inf)
      contentAsString(blogService.allBlogService) mustBe "Database is empty!"
    }
  }

  private def getBlogTitle(id: String): String = {
    val result = blogService.parseBSONObjectId(id, blogService.getBlogService)
    contentAsJson(result).apply("title").as[String]
  }
}
