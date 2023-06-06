package ru.itis
package helpers

import java.time.Instant

object TimeConverter {
  def addExpirationTimeToDate(
      date: Instant,
      expirationTime: String
  ): Instant = {
    expirationTime match {
      case TenMinutes.stringValue => TenMinutes.addExpirationTime(date)
      case OneHour.stringValue    => OneHour.addExpirationTime(date)
      case OneDay.stringValue     => OneDay.addExpirationTime(date)
      case OneWeek.stringValue    => OneWeek.addExpirationTime(date)
      case OneMonth.stringValue   => OneMonth.addExpirationTime(date)
      case OneYear.stringValue    => OneYear.addExpirationTime(date)
      case _                      => date
    }
  }
}
