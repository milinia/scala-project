package ru.itis
package helpers

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.time.temporal.ChronoUnit

sealed trait ExpirationTime {
  val stringValue: String
  def addExpirationTime(date: Instant): Instant
}

object TenMinutes extends ExpirationTime {
  override val stringValue: String = "10 minutes"
  override def addExpirationTime(date: Instant): Instant =
    date.plus(10, ChronoUnit.MINUTES)
}

object OneHour extends ExpirationTime {
  override val stringValue: String = "1 hour"
  override def addExpirationTime(date: Instant): Instant =
    date.plus(1, ChronoUnit.HOURS)
}

object OneDay extends ExpirationTime {
  override val stringValue: String = "1 day"
  override def addExpirationTime(date: Instant): Instant =
    date.plus(1, ChronoUnit.DAYS)
}

object OneWeek extends ExpirationTime {
  override val stringValue: String = "1 week"
  override def addExpirationTime(date: Instant): Instant =
    date.plus(7, ChronoUnit.DAYS)
}

object OneMonth extends ExpirationTime {
  override val stringValue: String = "1 month"
  override def addExpirationTime(date: Instant): Instant = {
    val localDateTime: LocalDateTime =
      LocalDateTime.ofInstant(date, ZoneOffset.UTC)
    val updatedLocalDateTime: LocalDateTime =
      localDateTime.plus(1, ChronoUnit.MONTHS)
    updatedLocalDateTime.toInstant(ZoneOffset.UTC)
  }
}

object OneYear extends ExpirationTime {
  override val stringValue: String = "1 year"
  override def addExpirationTime(date: Instant): Instant = {
    val localDateTime: LocalDateTime =
      LocalDateTime.ofInstant(date, ZoneOffset.UTC)
    val updatedLocalDateTime: LocalDateTime =
      localDateTime.plus(1, ChronoUnit.YEARS)
    updatedLocalDateTime.toInstant(ZoneOffset.UTC)
  }
}
