package services

import com.mongodb.client.result.UpdateResult
import models.{APIError, DataModel}
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.empty
import org.mongodb.scala.model.{Filters, ReplaceOptions, UpdateOptions}
import org.mongodb.scala.model.Updates.set
import org.mongodb.scala.result
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{Action, AnyContent, Request}
import repositories.DataRepository

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RepositoryService @Inject()(dataRepository: DataRepository){

  def index(): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]]  = {
    val result = dataRepository.index()
    result.map{
      case Left(error) => ???
      case Right(value) => ???
    }
  }


  def create(book: DataModel): Future[Either[APIError.BadAPIResponse, DataModel]] = ???


  def readByName(name: String): Future[Either[APIError.BadAPIResponse, Option[DataModel]]] = ???


  def read(id: String): Future[Either[APIError.BadAPIResponse, Option[DataModel]]] = ???


  def update(id: String, book: DataModel): Future[Either[APIError.BadAPIResponse, result.UpdateResult]] = ???


  def delete(id: String): Future[Either[APIError.BadAPIResponse, result.DeleteResult]] = ???


  def getUpdateOperation(field:String, value:String):Either[APIError.BadAPIResponse, Bson] = ???


  def updateField(id: String, field: String, value:String): Future[Either[APIError.BadAPIResponse, result.UpdateResult]] = ???


  def deleteAll(): Future[Unit] = ??? //Hint: needed for testst: needed for tests

}
