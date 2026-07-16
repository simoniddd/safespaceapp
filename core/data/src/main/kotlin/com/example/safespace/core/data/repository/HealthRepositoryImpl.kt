package com.example.safespace.core.data.repository

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.example.safespace.core.domain.model.health.DailySteps
import com.example.safespace.core.domain.model.health.ExerciseSession
import com.example.safespace.core.domain.model.health.SleepSummary
import com.example.safespace.core.domain.repository.HealthAvailability
import com.example.safespace.core.domain.repository.HealthRepository
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

internal class HealthRepositoryImpl(
    private val context: Context,
    private val client: HealthConnectClient?
) : HealthRepository {

    @OptIn(ExperimentalTime::class)
    override suspend fun getStepsForDate(date: LocalDate): DailySteps? {
        client ?: return null
        val startInstant = date
            .atStartOfDayIn(TimeZone.currentSystemDefault())
            .toJavaInstant()
        val endInstant = date
            .plus(1, DateTimeUnit.DAY)
            .atStartOfDayIn(TimeZone.currentSystemDefault())
            .toJavaInstant()

        val request = AggregateRequest(
            metrics = setOf(StepsRecord.COUNT_TOTAL),
            timeRangeFilter = TimeRangeFilter.between(startInstant, endInstant)
        )

        val response = client.aggregate(request)

        val count = response.get(StepsRecord.COUNT_TOTAL)
        return if (count == null) null else DailySteps(count.toInt(), date)
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun getSleepForDate(date: LocalDate): SleepSummary? {
        client ?: return null
        val yesterdayDate = date.minus(1, DateTimeUnit.DAY)
        val noonTime = LocalTime(hour = 12, minute = 0)
        val yesterdayNoon = LocalDateTime(yesterdayDate, noonTime)
        val todayNoon = LocalDateTime(date, noonTime)

        val startInstant = yesterdayNoon.toInstant(TimeZone.currentSystemDefault()).toJavaInstant()
        val endInstant = todayNoon.toInstant(TimeZone.currentSystemDefault()).toJavaInstant()

        val request = ReadRecordsRequest(
            recordType = SleepSessionRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startInstant, endInstant)
        )
        val records = client.readRecords(request).records
        val totalMinutes = records.sumOf { record ->
            Duration.between(record.startTime, record.endTime).toMinutes()
        }
        return if (records.isEmpty()) null else SleepSummary(totalMinutes.toInt(), date)
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun getExercisesForDate(date: LocalDate): List<ExerciseSession> {
        client ?: return emptyList()
        val startInstant = date
            .atStartOfDayIn(TimeZone.currentSystemDefault())
            .toJavaInstant()

        val endInstant = date
            .plus(1, DateTimeUnit.DAY)
            .atStartOfDayIn(TimeZone.currentSystemDefault())
            .toJavaInstant()

        val request = ReadRecordsRequest(
            recordType = ExerciseSessionRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startInstant, endInstant)
        )
        val records = client.readRecords(request).records

        val totalExerciseSession = records.map { record ->
            ExerciseSession(
                durationMinutes = Duration
                    .between(record.startTime, record.endTime).toMinutes().toInt(),
                title = record.title.orEmpty(),
                source = record.metadata.dataOrigin.packageName,
                startTime = record.startTime.toKotlinInstant()
                    .toLocalDateTime(TimeZone.currentSystemDefault())
            )
        }
        return totalExerciseSession
    }

    override suspend fun isAvailable(): HealthAvailability {
        return when (HealthConnectClient.getSdkStatus(context)) {
            HealthConnectClient.SDK_AVAILABLE -> {
                val granted = client?.permissionController?.getGrantedPermissions()
                if (granted?.containsAll(PERMISSIONS) == true) {
                    HealthAvailability.AVAILABLE
                } else {
                    HealthAvailability.NO_PERMISSION
                }
            }

            else -> HealthAvailability.NOT_INSTALLED
        }
    }

    companion object {
        val PERMISSIONS = setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getReadPermission(SleepSessionRecord::class),
            HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        )
    }
}