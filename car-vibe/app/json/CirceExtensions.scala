package json

import io.circe.syntax.EncoderOps
import io.circe.{Encoder, Json, Printer}
import play.api.libs.circe.Circe

trait CirceExtensions extends Circe {

  implicit val customPrinter: Printer = Printer.noSpaces.copy(dropNullValues = true)

  def asJson[E](value: E)(implicit encoder: Encoder[E]): Json = value.asJson
}
