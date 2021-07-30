package models.errors

sealed abstract class BusinessError

final case class ValidationErrors(errors: List[String]) extends BusinessError
final case class GenericErrors(errors: List[String]) extends BusinessError

