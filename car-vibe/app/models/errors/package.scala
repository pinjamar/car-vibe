package models

import io.circe.{Encoder, Json}
import io.circe.syntax._

package object errors {
  implicit val errorCoder: Encoder[BusinessError] = (err: BusinessError) => Json.obj(
    err match {
      case ValidationErrors(e) => ("validation_errors", e.asJson)
      case GenericErrors(e) => ("errors", e.asJson)
    }
  )
}
