package connectors

import cats.data.EitherT
import models.APIError
import play.api.libs.json.OFormat
import play.api.libs.ws.{WSClient, WSResponse}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LibraryConnector @Inject()(ws: WSClient) {
  def get[Response](url: String)(implicit rds: OFormat[Response], ec: ExecutionContext): EitherT[Future, APIError, Response] = {
    val request = ws.url(url)
    val response = request.get()
    EitherT {
      response.map { result =>
        println(s"Received JSON: ${result.json}")  // Log the JSON response
        Right(result.json.as[Response])
      }.recover { case ex: Throwable =>
        println(s"Error: ${ex.getMessage}")  // Log any errors
        Left(APIError.BadAPIResponse(500, "Could not connect"))
      }
    }
  }
}
