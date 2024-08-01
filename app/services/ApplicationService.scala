package services

import cats.data.EitherT
import connectors.LibraryConnector
import models.{APIError, DataModel}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApplicationService @Inject()(connector: LibraryConnector) {

  def getGoogleBook(urlOverride: Option[String] = None, search: String, term: String)(implicit ec: ExecutionContext):EitherT[Future, APIError, Book] = {
    val result = connector.get[DataModel](urlOverride.getOrElse(s"https://www.googleapis.com/books/v1/volumes?q=$search%$term"))
    result.map { dataModel =>
      Book(
        isbn = dataModel.id,
        title = dataModel.title,
        subtitle = dataModel.subtitle,
        pageCount = dataModel.pageCount
      )
    }
  }

}
