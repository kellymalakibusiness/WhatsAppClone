package common

import kotlinx.datetime.LocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
val todayNowDate = Instant.parse("2025-07-20T12:00:00.000000Z")
val day1Time1 = LocalDateTime.parse("2025-07-20T08:00:00")
val day1Time2 = LocalDateTime.parse("2025-07-20T09:00:00")
val day1Time3 = LocalDateTime.parse("2025-07-20T10:00:00")
val day1Time4 = LocalDateTime.parse("2025-07-20T11:00:00")

val day2Time1 = LocalDateTime.parse("2025-07-19T08:00:00")
val day2Time2 = LocalDateTime.parse("2025-07-19T09:00:00")
val day2Time3 = LocalDateTime.parse("2025-07-19T10:00:00")
val day2Time4 = LocalDateTime.parse("2025-07-19T11:00:00")

val day3Time1 = LocalDateTime.parse("2025-07-18T08:00:00")
val day3Time2 = LocalDateTime.parse("2025-07-18T09:00:00")
val day3Time3 = LocalDateTime.parse("2025-07-18T10:00:00")
val day3Time4 = LocalDateTime.parse("2025-07-18T11:00:00")

val day4Time1 = LocalDateTime.parse("2025-07-12T08:00:00")
val day4Time2 = LocalDateTime.parse("2025-07-12T09:00:00")
val day4Time3 = LocalDateTime.parse("2025-07-12T10:00:00")
val day4Time4 = LocalDateTime.parse("2025-07-12T11:00:00")