package services

import connectors.LibraryConnector
import models.DataModel
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApplicationService @Inject()(connector: LibraryConnector) {

  def getGoogleBook(urlOverride: Option[String] = None, search: String, term: String)(implicit ec: ExecutionContext): Future[Book] = {
    connector.get[DataModel](urlOverride.getOrElse(s"https://www.googleapis.com/books/v1/volumes?q=$search%$term")).flatMap{
      dataModel: DataModel =>
        val newBook:Book = Book(_id = dataModel._id, name = dataModel.name, description = dataModel.description, pageCount = dataModel.pageCount)
        Future(newBook)
    }
  }

}
