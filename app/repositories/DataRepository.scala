package repositories

import models.{APIError, DataModel}
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.{empty, equal}
import org.mongodb.scala.model.Updates.set
import org.mongodb.scala.model._
import org.mongodb.scala.result
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.runtime.universe.Try
import scala.util.{Failure, Success}

@Singleton
class DataRepository @Inject()(
                                mongoComponent: MongoComponent
                              )(implicit ec: ExecutionContext) extends PlayMongoRepository[DataModel](
  collectionName = "dataModels",
  mongoComponent = mongoComponent,
  domainFormat = DataModel.formats,
  indexes = Seq(IndexModel(
    Indexes.ascending("_id")
  )),
  replaceIndexes = false
) {

  def index(): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]]  =
    collection.find().toFuture().map { books =>
      Right(books)
    }.recover {
      case ex: Exception => Left(APIError.BadAPIResponse(500, s"An error occurred: ${ex.getMessage}"))
    }

  def create(book: DataModel): Future[Either[APIError.BadAPIResponse, DataModel]] =
    collection
      .insertOne(book)
      .toFuture().map(_ => Right(book)
      ).recover{
        case ex: Exception => Left(APIError.BadAPIResponse(500, s"An error occurred: ${ex.getMessage}"))
      }

  private def byID(id: String): Bson =
    Filters.and(
      Filters.equal("_id", id)
    )

  private def byName(name:String):Bson =
    Filters.and(
      Filters.equal("name", name)
    )

  def readByName(name: String): Future[Either[APIError.BadAPIResponse, Option[DataModel]]] =
    collection.find(byName(name)).headOption.map { data =>
      Right(data)
    }.recover {
      case ex: Exception => Left(APIError.BadAPIResponse(500, s"An error occurred: ${ex.getMessage}"))
    }

  def read(id: String): Future[Either[APIError.BadAPIResponse, Option[DataModel]]] =
    collection.find(byID(id)).headOption.map { data =>
      Right(data)
    }.recover {
      case ex: Exception => Left(APIError.BadAPIResponse(500, s"An error occurred: ${ex.getMessage}"))
    }

  def update(id: String, book: DataModel): Future[Either[APIError.BadAPIResponse, result.UpdateResult]] =
    collection.replaceOne(
      filter = byID(id),
      replacement = book,
      options = new ReplaceOptions().upsert(true) // if upsert set to false, no document created if no match, will throw error
    ).toFuture().map(Right(_)).recover {
      case ex: Exception => Left(APIError.BadAPIResponse(500, s"An error occurred: ${ex.getMessage}"))
    }

  def delete(id: String): Future[Either[APIError.BadAPIResponse, result.DeleteResult]] =
    collection.deleteOne(byID(id)).toFuture().map { deleteResult =>
      if (deleteResult.getDeletedCount > 0) Right(deleteResult)
      else Left(APIError.BadAPIResponse(404, s"No item found with id: $id"))
    }.recover {
      case ex: Exception => Left(APIError.BadAPIResponse(500, s"An error occurred: ${ex.getMessage}"))
    }

  def getItemToUpdate(id: String): Future[Either[APIError.BadAPIResponse, DataModel]] = {
    collection.find(equal("_id", id)).first().toFuture().map { result =>
      Right(result)
    }.recover {
      case ex: Exception => Left(APIError.BadAPIResponse(500, s"An error occurred: ${ex.getMessage}"))
    }
  }

  def getUpdateOperation(updateField:UpdateField, updatedValue:String):Either[APIError.BadAPIResponse, Bson] = {
    updateField match {
      case UpdateField.Name => Right(set("name", updatedValue))
      case UpdateField.Description => Right(set("description", updatedValue))
      case UpdateField.PageCount =>
        try{
          Right(set("pageCount", updatedValue.toInt))
        } case _ => Left(APIError.BadAPIResponse(500, "incorrect value given"))
        }
    }

  def updateField(id: String, update: Update): Future[Either[APIError.BadAPIResponse, result.UpdateResult]] = {
    getUpdateOperation(update.fieldToUpdate, update.updatedValue) match {
      case Left(error) => Future(Left(error))
      case Right(updateOperation) =>
        collection.updateOne(
          filter = byID(id),
          update = updateOperation,
          options = new UpdateOptions().upsert(false)
        ).toFuture().map(Right(_)).recover{
          case ex: Exception => Left(APIError.BadAPIResponse(500, s"An error occurred: ${ex.getMessage}"))
        }
    }
  }



  def deleteAll(): Future[Unit] = collection.deleteMany(empty()).toFuture().map(_ => ()) //Hint: needed for testst: needed for tests


}
