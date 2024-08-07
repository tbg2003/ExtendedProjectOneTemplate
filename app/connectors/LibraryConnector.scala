package connectors

import cats.data.EitherT
import models.APIError
import play.api.libs.json.OFormat
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LibraryConnector @Inject()(ws: WSClient) {

  def get[Response](url: String)(implicit rds: OFormat[Response], ec: ExecutionContext): EitherT[Future, APIError, Response] = {
    val request: WSRequest = ws.url(url)
    val response: Future[WSResponse] = request.get()

    EitherT {
      response.map { result =>
        if (result.status == 200) {
          Right(result.json.as[Response])
        } else {
          Left(APIError.BadAPIResponse(result.status, result.statusText))
        }
      }.recover { case ex: Throwable =>
        Left(APIError.BadAPIResponse(500, "No Response from Google Books API"))
      }
    }
  }
}

