package services

import com.mongodb.client.result.UpdateResult
import models.{APIError, DataModel, UpdateBook}
import org.mongodb.scala.result
import repositories.MockRepository

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RepositoryService @Inject()(mockRepository: MockRepository){

  def index()(implicit ec: ExecutionContext): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]]  = {
    mockRepository.index().map{
      case Left(error) => Left(error)
      case Right(item: Seq[DataModel]) => Right(item)
      case Right(_) => Left(APIError.BadAPIResponse(500, "unexpected error occurred"))
    }
  }

  def create(book: DataModel)(implicit ec: ExecutionContext): Future[Either[APIError.BadAPIResponse, DataModel]] = {
    mockRepository.create(book).map{
      case Left(error) => Left(error)
      case Right(item : DataModel) => Right(item)
      case Right(_) => Left(APIError.BadAPIResponse(500, "unexpected error occurred"))
    }
  }

  def readByName(name: String)(implicit ec: ExecutionContext): Future[Either[APIError.BadAPIResponse, Option[DataModel]]] = {
    mockRepository.readByName(name).map{
      case Left(error) => Left(error)
      case Right(Some(item:DataModel)) => Right(Some(item))
      case Right(Some(error)) => Left(APIError.BadAPIResponse(500, s"Error: $error not of type DataModel"))
      case Right(None) => Left(APIError.BadAPIResponse(404, s"No Book Found with name: $name"))
    }
  }

  def readByISBN(isbn: String)(implicit ec: ExecutionContext): Future[Either[APIError.BadAPIResponse, Option[DataModel]]] = {
    mockRepository.readByISBN(isbn).map{
      case Left(error) => Left(error)
      case Right(Some(item:DataModel)) => Right(Some(item))
      case Right(Some(error)) => Left(APIError.BadAPIResponse(500, s"Error: $error not of type DataModel"))
      case Right(None) => Left(APIError.BadAPIResponse(404, s"No Book Found with isbn: $isbn"))
    }
  }

  def read(id: String)(implicit ec: ExecutionContext): Future[Either[APIError.BadAPIResponse, Option[DataModel]]] = {
    mockRepository.read(id).map{
      case Left(error) => Left(error)
      case Right(Some(item:DataModel)) => Right(Some(item))
      case Right(Some(error)) => Left(APIError.BadAPIResponse(500, s"Error: $error not of type DataModel"))
      case Right(None) => Left(APIError.BadAPIResponse(404, s"No Book Found with id: $id"))
    }
  }

  def update(id: String, book: DataModel)(implicit ec: ExecutionContext): Future[Either[APIError.BadAPIResponse, result.UpdateResult]] = {
    mockRepository.update(id, book).map {
      case Left(error) => Left(error)
      case Right(result: UpdateResult) =>
        if(result.wasAcknowledged()) Right(result)
        else Left(APIError.BadAPIResponse(404, s"$result Not Found"))
      case Right(_) => Left(APIError.BadAPIResponse(500, "unexpected error occurred"))
      }
    }

  def delete(id: String)(implicit ec: ExecutionContext): Future[Either[APIError.BadAPIResponse, result.DeleteResult]] = {
    mockRepository.delete(id).map {
      case Left(error) => Left(error)
      case Right(value) => Right(value)
    }
  }

  def updateField(id: String, field: String, value:String)(implicit ec: ExecutionContext): Future[Either[APIError.BadAPIResponse, result.UpdateResult]] = {
    val cleanField:String = field.strip().toLowerCase
    val cleanValue:String = value.strip().toLowerCase
    mockRepository.updateField(id, cleanField, cleanValue).map {
      case Left(error) => Left(error)
      case Right(result) =>
        if (result.getMatchedCount > 0) Right(result)
        else Left(APIError.BadAPIResponse(404, s"No item found with id: $id"))
    }
  }

  def makeUpdates(book: DataModel, updates: UpdateBook)(implicit ec: ExecutionContext): Future[Either[APIError.BadAPIResponse, result.UpdateResult]] = {
    val updatedBook = DataModel(
      _id = book._id,
      title = updates.title.getOrElse(book.title),
      subtitle = updates.subtitle.getOrElse(book.subtitle),
      pageCount = updates.pageCount.getOrElse(book.pageCount)
    )
    mockRepository.update(book._id, updatedBook)
  }
}
