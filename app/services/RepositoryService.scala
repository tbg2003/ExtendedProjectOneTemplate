package services

import cats.data.EitherT
import com.mongodb.client.result.UpdateResult
import models.{APIError, DataModel}
import org.mongodb.scala.result
import repositories.DataRepository

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RepositoryService @Inject()(dataRepository: DataRepository){

  def index()(implicit ec: ExecutionContext): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]]  = {
    dataRepository.index().map{
      case Left(error) => Left(error)
      case Right(item: Seq[DataModel]) => Right(item)
      case Right(_) => Left(APIError.BadAPIResponse(500, "unexpected error occurred"))
    }
  }

  def create(book: DataModel)(implicit ec: ExecutionContext): Future[Either[APIError.BadAPIResponse, DataModel]] = {
    dataRepository.create(book).map{
      case Left(error) => Left(error)
      case Right(item : DataModel) => Right(item)
      case Right(_) => Left(APIError.BadAPIResponse(500, "unexpected error occurred"))
    }
  }


  def readByName(name: String)(implicit ec: ExecutionContext): Future[Either[APIError.BadAPIResponse, Option[DataModel]]] = {
    dataRepository.readByName(name).map{
      case Left(error) => Left(error)
      case Right(Some(item:DataModel)) => Right(Some(item))
      case Right(Some(error)) => Left(APIError.BadAPIResponse(500, s"Error: $error not of type DataModel"))
      case Right(None) => Left(APIError.BadAPIResponse(404, s"No Book Found with name: $name"))
    }
  }


  def read(id: String)(implicit ec: ExecutionContext): Future[Either[APIError.BadAPIResponse, Option[DataModel]]] = {
    dataRepository.read(id).map{
      case Left(error) => Left(error)
      case Right(Some(item:DataModel)) => Right(Some(item))
      case Right(Some(error)) => Left(APIError.BadAPIResponse(500, s"Error: $error not of type DataModel"))
      case Right(None) => Left(APIError.BadAPIResponse(404, s"No Book Found with id: $id"))
    }
  }


  def update(id: String, book: DataModel)(implicit ec: ExecutionContext): Future[Either[APIError.BadAPIResponse, result.UpdateResult]] = {
    dataRepository.update(id, book).map {
      case Left(error) => Left(error)
      case Right(result: UpdateResult) =>
        if( result.wasAcknowledged()) Right(result)
        else Left(APIError.BadAPIResponse(404, s"$result Not Found"))
      case Right(_) => Left(APIError.BadAPIResponse(500, "unexpected error occurred"))
      }
    }


  def delete(id: String)(implicit ec: ExecutionContext): Future[Either[APIError.BadAPIResponse, result.DeleteResult]] = {
    dataRepository.delete(id).map {
      case Left(error) => Left(error)
      case Right(value) => Right(value)
    }
  }

  def updateField(id: String, field: String, value:String)(implicit ec: ExecutionContext): Future[Either[APIError.BadAPIResponse, result.UpdateResult]] = {
    dataRepository.updateField(id, field, value).map {
      case Left(error) => Left(error)
      case Right(value) => Right(value)
    }
  }

}