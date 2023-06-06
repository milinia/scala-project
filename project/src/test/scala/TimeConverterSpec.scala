package ru.itis

import helpers.TimeConverter

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.time.temporal.ChronoUnit

class TimeConverterSpec extends AnyFlatSpec with Matchers {

  "TimeConverter class" should "correct add 1 minute to date" in {
    val now = Instant.now()
    val future =
      TimeConverter.addExpirationTimeToDate(now, "10 minutes")
    future shouldEqual now.plus(10, ChronoUnit.MINUTES)
  }

  "TimeConverter class" should "correct add 1 hour to date" in {
    val now = Instant.now()
    val future =
      TimeConverter.addExpirationTimeToDate(now, "1 hour")
    future shouldEqual now.plus(1, ChronoUnit.HOURS)
  }

  "TimeConverter class" should "correct add 1 day to date" in {
    val now = Instant.now()
    val future =
      TimeConverter.addExpirationTimeToDate(now, "1 day")
    future shouldEqual now.plus(1, ChronoUnit.DAYS)
  }

  "TimeConverter class" should "correct add 1 week to date" in {
    val now = Instant.now()
    val future =
      TimeConverter.addExpirationTimeToDate(now, "1 week")
    future shouldEqual now.plus(7, ChronoUnit.DAYS)
  }

  "TimeConverter class" should "correct add 1 month to date" in {
    val now = Instant.now()
    val future =
      TimeConverter.addExpirationTimeToDate(now, "1 month")
    val localDateTime: LocalDateTime =
      LocalDateTime.ofInstant(now, ZoneOffset.UTC)
    val updatedLocalDateTime: LocalDateTime =
      localDateTime.plus(1, ChronoUnit.MONTHS)
    val updatedInstant = updatedLocalDateTime.toInstant(ZoneOffset.UTC)
    future shouldEqual updatedInstant
  }

  "TimeConverter class" should "correct add 1 year to date" in {
    val now = Instant.now()
    val future =
      TimeConverter.addExpirationTimeToDate(now, "1 year")
    val localDateTime: LocalDateTime =
      LocalDateTime.ofInstant(now, ZoneOffset.UTC)
    val updatedLocalDateTime: LocalDateTime =
      localDateTime.plus(1, ChronoUnit.YEARS)
    val updatedInstant = updatedLocalDateTime.toInstant(ZoneOffset.UTC)
    future shouldEqual updatedInstant
  }
}
