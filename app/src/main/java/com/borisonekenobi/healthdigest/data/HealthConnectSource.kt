package com.borisonekenobi.healthdigest.data

import android.content.Context
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.records.NutritionRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.borisonekenobi.healthdigest.HealthConnectManager
import com.borisonekenobi.healthdigest.model.Activity
import com.borisonekenobi.healthdigest.model.BodyMetrics
import com.borisonekenobi.healthdigest.model.Nutrition
import com.borisonekenobi.healthdigest.model.Recovery
import com.borisonekenobi.healthdigest.model.WaistFit
import java.time.Duration
import java.time.LocalDateTime
import java.time.Period

class HealthConnectSource(context: Context) {
    private val healthConnectManager = HealthConnectManager(context)
    private val healthConnectPermissions = HealthConnectPermissions(context)

    suspend fun getBodyInformation(
        start: LocalDateTime, end: LocalDateTime, waistFit: WaistFit
    ): BodyMetrics {
        if (!healthConnectPermissions.hasPermission(HealthPermission.getReadPermission(WeightRecord::class)))
            return BodyMetrics(null, null, null, null)

        val response = healthConnectManager.client.aggregateGroupByPeriod(
            AggregateGroupByPeriodRequest(
                metrics = setOf(WeightRecord.WEIGHT_AVG),
                timeRangeFilter = TimeRangeFilter.between(start, end),
                timeRangeSlicer = Period.ofDays(1),
            )
        )

        val currentWeight = response.lastOrNull()?.result?.get(WeightRecord.WEIGHT_AVG)?.inKilograms
        val previousWeight =
            response.firstOrNull()?.result?.get(WeightRecord.WEIGHT_AVG)?.inKilograms
        val weightChange = currentWeight?.minus(previousWeight ?: 0.0)

        return BodyMetrics(
            currentWeightKg = currentWeight,
            previousWeightKg = previousWeight,
            weightChangeKg = weightChange,
            waistFit = waistFit,
        )
    }

    suspend fun getNutritionInformation(start: LocalDateTime, end: LocalDateTime): Nutrition {
        if (!healthConnectPermissions.hasPermission(HealthPermission.getReadPermission(NutritionRecord::class)) ||
            !healthConnectPermissions.hasPermission(HealthPermission.getReadPermission(HydrationRecord::class)))
            return Nutrition(null, null, null, null, null, null, null, null)

        val response = healthConnectManager.client.aggregateGroupByPeriod(
            AggregateGroupByPeriodRequest(
                metrics = setOf(
                    NutritionRecord.ENERGY_TOTAL,
                    NutritionRecord.PROTEIN_TOTAL,
                    NutritionRecord.TOTAL_CARBOHYDRATE_TOTAL,
                    NutritionRecord.TOTAL_FAT_TOTAL,
                    HydrationRecord.VOLUME_TOTAL,
                ),
                timeRangeFilter = TimeRangeFilter.between(start, end),
                timeRangeSlicer = Period.ofDays(1),
            )
        )

        val totalCalories = response.sumOf {
            it.result[NutritionRecord.ENERGY_TOTAL]?.inKilocalories ?: 0.0
        }
        val averageCalories = if (response.isEmpty()) 0.0 else totalCalories / response.size
        val totalProteinGrams = response.sumOf {
            it.result[NutritionRecord.PROTEIN_TOTAL]?.inGrams ?: 0.0
        }
        val averageProteinGrams = if (response.isEmpty()) 0.0 else totalProteinGrams / response.size
        val totalCarbsGrams = response.sumOf {
            it.result[NutritionRecord.TOTAL_CARBOHYDRATE_TOTAL]?.inGrams ?: 0.0
        }
        val averageCarbsGrams = if (response.isEmpty()) 0.0 else totalCarbsGrams / response.size
        val totalFatGrams = response.sumOf {
            it.result[NutritionRecord.TOTAL_FAT_TOTAL]?.inGrams ?: 0.0
        }
        val averageFatGrams = if (response.isEmpty()) 0.0 else totalFatGrams / response.size
        val totalWaterLitres = response.sumOf {
            it.result[HydrationRecord.VOLUME_TOTAL]?.inLiters ?: 0.0
        }
        val averageWaterLitres = if (response.isEmpty()) 0.0 else totalWaterLitres / response.size
        val daysLogged = response.size
        // TODO: calculate these values
        val daysWithinCalorieGoal = 0
        val daysProteinGoalMet = 0

        return Nutrition(
            averageCalories = averageCalories,
            averageProteinGrams = averageProteinGrams,
            averageCarbsGrams = averageCarbsGrams,
            averageFatGrams = averageFatGrams,
            averageWaterLitres = averageWaterLitres,
            daysLogged = daysLogged,
            daysWithinCalorieGoal = daysWithinCalorieGoal,
            daysProteinGoalMet = daysProteinGoalMet,
        )
    }

    suspend fun getActivityInformation(start: LocalDateTime, end: LocalDateTime): Activity {
        if (!healthConnectPermissions.hasPermission(HealthPermission.getReadPermission(StepsRecord::class)) ||
            !healthConnectPermissions.hasPermission(HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class)) ||
            !healthConnectPermissions.hasPermission(HealthPermission.getReadPermission(ExerciseSessionRecord::class)))
            return Activity(null, null, null, null, null)

        val response = healthConnectManager.client.aggregateGroupByPeriod(
            AggregateGroupByPeriodRequest(
                metrics = setOf(
                    StepsRecord.COUNT_TOTAL,
                    ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL,
                    ExerciseSessionRecord.EXERCISE_DURATION_TOTAL,
                ),
                timeRangeFilter = TimeRangeFilter.between(start, end),
                timeRangeSlicer = Period.ofDays(1),
            )
        )

        val totalSteps = response.sumOf { it.result[StepsRecord.COUNT_TOTAL] ?: 0 }
        val averageStepsPerDay = if (response.isEmpty()) 0 else totalSteps / response.size
        val activeCalories = response.sumOf {
            it.result[ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL]?.inKilocalories ?: 0.0
        }
        val exerciseDuration = response.sumOf {
            it.result[ExerciseSessionRecord.EXERCISE_DURATION_TOTAL]?.toMinutes() ?: 0
        }
        // TODO: calculate this value
        val workoutCount = 0

        return Activity(
            totalSteps = totalSteps,
            averageStepsPerDay = averageStepsPerDay,
            activeCalories = activeCalories,
            exerciseMinutes = exerciseDuration,
            workoutCount = workoutCount,
        )
    }

    suspend fun getRecoveryInformation(start: LocalDateTime, end: LocalDateTime): Recovery {
        if (!healthConnectPermissions.hasPermission(HealthPermission.getReadPermission(SleepSessionRecord::class)) ||
            !healthConnectPermissions.hasPermission(HealthPermission.getReadPermission(HeartRateRecord::class)))
            return Recovery(null, null)

        val response = healthConnectManager.client.aggregateGroupByPeriod(
            AggregateGroupByPeriodRequest(
                metrics = setOf(
                    SleepSessionRecord.SLEEP_DURATION_TOTAL,
                    HeartRateRecord.MEASUREMENTS_COUNT,
                    HeartRateRecord.BPM_AVG,
                ),
                timeRangeFilter = TimeRangeFilter.between(start, end),
                timeRangeSlicer = Period.ofDays(1),
            )
        )

        val totalSleep = response.sumOf {
            it.result[SleepSessionRecord.SLEEP_DURATION_TOTAL]?.toMinutes() ?: 0
        }
        val averageSleep =
            if (response.isEmpty()) Duration.ofMinutes(0) else Duration.ofMinutes(totalSleep / response.size)
        val heartRateCount = response.sumOf { it.result[HeartRateRecord.MEASUREMENTS_COUNT] ?: 0 }
        val averageHeartRate =
            response.sumOf { it.result[HeartRateRecord.BPM_AVG] ?: 0 } / heartRateCount

        return Recovery(
            averageSleep = averageSleep,
            averageHeartRate = averageHeartRate,
        )
    }
}
