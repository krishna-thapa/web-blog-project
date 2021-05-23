package models

import org.joda.time.DateTime

trait WithDate {
  def createdDate: Option[DateTime]
  def updatedDate: Option[DateTime]
}
