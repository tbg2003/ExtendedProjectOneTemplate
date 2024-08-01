package models

import play.api.http.Status
import play.api.libs.json.{Json, OFormat}

sealed abstract class APIError(
                                val httpResponseStatus: Int,
                                val reason: String
                              )

object APIError {

  final case class BadAPIResponse(upstreamStatus: Int, upstreamMessage: String)
    extends APIError(
      Status.INTERNAL_SERVER_ERROR,
      s"Bad response from upstream; got status: $upstreamStatus, and got reason $upstreamMessage"
    )

  implicit val format: OFormat[APIError] = Json.format[APIError]
}