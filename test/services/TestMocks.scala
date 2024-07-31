package services

import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.mongo.MongoComponent

import scala.concurrent.ExecutionContext

trait TestMocks extends MockFactory {
  val mockMongoComponent: MongoComponent = mock[MongoComponent]
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
}