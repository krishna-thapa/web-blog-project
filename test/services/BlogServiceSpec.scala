package services

import com.dimafeng.testcontainers.MongoDBContainer
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Result
import play.api.test.Helpers.{ contentAsString, defaultAwaitTimeout }

import scala.concurrent.Future

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

  "Default Mongo container" must {
    "start in default port" in {
      assert(container.replicaSetUrl.nonEmpty)
    }
  }

  "CRUD operation in mongoDB" must {
    "Should have an empty when initialized" in {
      val result: Future[Result] = blogService.allBlogService
      contentAsString(result) mustBe "Database is empty!"
    }
  }

}
