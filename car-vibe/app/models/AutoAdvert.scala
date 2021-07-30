package models

case class AutoAdvert(
  id: Long,
  title: String,
  fuelType: String,
  price: Double,
  mileage: Option[Long],
  isNew: Boolean,
  firstRegistration: Option[String]
)
