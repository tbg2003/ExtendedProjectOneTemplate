package repositories

sealed trait UpdateField

object UpdateField{
  case object Id extends UpdateField
  case object Name extends UpdateField
  case object Description extends UpdateField
  case object PageCount extends UpdateField
}