package models

case class CreateUpdateAutoAdvert(
  title: String,
  fuelType: String,
  price: Double,
  mileage: Option[Long],
  isNew: Boolean,
  firstRegistration: Option[String]
)