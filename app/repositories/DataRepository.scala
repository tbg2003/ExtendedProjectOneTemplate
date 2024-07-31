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


  def getUpdateOperation(field:String, value:String):Either[APIError.BadAPIResponse, Bson] = {
    field match {
      case "name" => Right(set("name", value))
      case "description" => Right(set("description", value))
      case "pagecount" =>
        try{
          Right(set("pageCount", value.toInt))
        }catch{ case ex:Exception => Left(APIError.BadAPIResponse(500, "pageCount must be an integer"))}
      case _ => Left(APIError.BadAPIResponse(500, "invalid field name"))
        }
    }

  def updateField(id: String, field: String, value:String): Future[Either[APIError.BadAPIResponse, result.UpdateResult]] = {
    getUpdateOperation(field, value) match {
      case Left(error) => Future(Left(error))
      case Right(updateOperation) =>
        collection.updateOne(
          filter = byID(id),
          update = updateOperation,
          options = new UpdateOptions().upsert(true)
        ).toFuture().
          map { updatedResult =>
            if (updatedResult.getMatchedCount > 0) Right(updatedResult)
            else Left(APIError.BadAPIResponse(404, s"No item found with id: $id"))
          }.recover{
          case ex: Exception => Left(APIError.BadAPIResponse(500, s"An error occurred: ${ex.getMessage}"))
        }
    }
  }



  def deleteAll(): Future[Unit] = collection.deleteMany(empty()).toFuture().map(_ => ()) //Hint: needed for testst: needed for tests


}
