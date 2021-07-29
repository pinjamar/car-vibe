package json

import akka.util.ByteString
import com.google.common.base.Charsets
import io.circe._
import io.circe.syntax._
import play.api.http._

trait CirceExtensions extends Status {

  private val jsonPrinter = Printer.noSpaces.copy(dropNullValues = true)

  private implicit val jsonContentType: ContentTypeOf[Json] = ContentTypeOf(Some(ContentTypes.JSON))
  implicit var circeJsonWritable: Writeable[Json] = Writeable[Json](e => ByteString.fromString(
    jsonPrinter.print(e),
    Charsets.UTF_8
  ))

  def asJson[E](value: E)(implicit encoder: Encoder[E]): Json = value.asJson
}
