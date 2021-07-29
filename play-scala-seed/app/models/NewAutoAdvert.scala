package models

case class NewAutoAdvert(
id: Option[Long],
title: String,
fuelType: String,
price: Double,
isNew: Boolean,
mileage: Option[Int],
firstRegistration: Option[String]
)