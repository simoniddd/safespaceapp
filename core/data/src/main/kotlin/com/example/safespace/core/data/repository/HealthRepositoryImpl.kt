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
import com.example.safespace.core.domain.model.health.ExerciseSession
import com.example.safespace.core.domain.model.health.HealthDataType
import com.example.safespace.core.domain.model.health.HealthDayData
import com.example.safespace.core.domain.model.health.HealthState
import com.example.safespace.core.domain.model.health.SleepSummary
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
    private val client: HealthConnectClient?,
) : HealthRepository {

    override suspend fun getState(date: LocalDate): HealthState {
        val sdkAvailable = HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE
        if (!sdkAvailable || client == null) return HealthState.NotInstalled

        val granted = client.permissionController.getGrantedPermissions()
        val missing = PERMISSION_BY_TYPE
            .filterValues { it !in granted }
            .keys
        if (missing.isNotEmpty()) return HealthState.NoPermission(toRequest = missing)

        return HealthState.Available(data = readDayData(client, date))
    }

    override fun permissionsFor(types: Set<HealthDataType>): Set<String> =
        types.mapNotNull { PERMISSION_BY_TYPE[it] }.toSet()

    private suspend fun readDayData(client: HealthConnectClient, date: LocalDate): HealthDayData =
        HealthDayData(
            steps = readSteps(client, date),
            sleep = readSleep(client, date),
            exercises = readExercises(client, date),
        )

    @OptIn(ExperimentalTime::class)
    private suspend fun readSteps(client: HealthConnectClient, date: LocalDate): Int? {
        val tz = TimeZone.currentSystemDefault()
        val start = date.atStartOfDayIn(tz).toJavaInstant()
        val end = date.plus(1, DateTimeUnit.DAY).atStartOfDayIn(tz).toJavaInstant()

        val response = client.aggregate(
            AggregateRequest(
                metrics = setOf(StepsRecord.COUNT_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(start, end),
            )
        )
        return response[StepsRecord.COUNT_TOTAL]?.toInt()
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun readSleep(client: HealthConnectClient, date: LocalDate): SleepSummary? {
        val tz = TimeZone.currentSystemDefault()
        val yesterdayNoon = LocalDateTime(date.minus(1, DateTimeUnit.DAY), NOON)
        val todayNoon = LocalDateTime(date, NOON)

        val records = client.readRecords(
            ReadRecordsRequest(
                recordType = SleepSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(
                    yesterdayNoon.toInstant(tz).toJavaInstant(),
                    todayNoon.toInstant(tz).toJavaInstant(),
                ),
            )
        ).records

        if (records.isEmpty()) return null

        val totalMinutes = records.sumOf { record ->
            Duration.between(record.startTime, record.endTime).toMinutes()
        }
        return SleepSummary(durationMinutes = totalMinutes.toInt(), date = date)
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun readExercises(client: HealthConnectClient, date: LocalDate): List<ExerciseSession> {
        val tz = TimeZone.currentSystemDefault()
        val start = date.atStartOfDayIn(tz).toJavaInstant()
        val end = date.plus(1, DateTimeUnit.DAY).atStartOfDayIn(tz).toJavaInstant()

        return client.readRecords(
            ReadRecordsRequest(
                recordType = ExerciseSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(start, end),
            )
        ).records.map { record ->
            ExerciseSession(
                durationMinutes = Duration.between(record.startTime, record.endTime).toMinutes().toInt(),
                title = record.title.orEmpty(),
                source = record.metadata.dataOrigin.packageName,
                startTime = record.startTime.toKotlinInstant().toLocalDateTime(tz),
            )
        }
    }

    private companion object {
        val NOON: LocalTime = LocalTime(hour = 12, minute = 0)

        val PERMISSION_BY_TYPE: Map<HealthDataType, String> = mapOf(
            HealthDataType.STEPS to HealthPermission.getReadPermission(StepsRecord::class),
            HealthDataType.SLEEP to HealthPermission.getReadPermission(SleepSessionRecord::class),
            HealthDataType.EXERCISE to HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        )
    }
}
