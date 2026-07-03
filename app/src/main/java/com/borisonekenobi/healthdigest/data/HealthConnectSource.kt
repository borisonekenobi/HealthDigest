package com.borisonekenobi.healthdigest.data

import android.content.Context
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.records.NutritionRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.Volume
import com.borisonekenobi.healthdigest.HealthConnectManager
import com.borisonekenobi.healthdigest.model.Activity
import com.borisonekenobi.healthdigest.model.BodyMetrics
import com.borisonekenobi.healthdigest.model.Nutrition
import com.borisonekenobi.healthdigest.model.Recovery
import com.borisonekenobi.healthdigest.model.Units
import com.borisonekenobi.healthdigest.model.WaistFit
import java.time.Duration
import java.time.LocalDateTime
import java.time.Period
import kotlin.reflect.KClass

class HealthConnectSource(context: Context) {
    private val healthConnectManager = HealthConnectManager(context)
    private val healthConnectPermissions = HealthConnectPermissions(context)

    suspend fun getBodyInformation(
        start: LocalDateTime, end: LocalDateTime, waistFit: WaistFit, units: Units
    ): BodyMetrics {
        if (!hasPermissions(setOf(WeightRecord::class))) {
            return BodyMetrics(
                null, null, null, null, units
            )
        }

        val response = healthConnectManager.client.aggregateGroupByPeriod(
            AggregateGroupByPeriodRequest(
                metrics = setOf(WeightRecord.WEIGHT_AVG),
                timeRangeFilter = TimeRangeFilter.between(start, end),
                timeRangeSlicer = Period.ofDays(1),
            )
        )

        val currentWeight = response.lastOrNull()?.result?.get(WeightRecord.WEIGHT_AVG)
        val previousWeight = response.firstOrNull()?.result?.get(WeightRecord.WEIGHT_AVG)
        val weightChange = if (currentWeight == null || previousWeight == null) null
        else Mass.kilograms(currentWeight.inKilograms - previousWeight.inKilograms)

        return BodyMetrics(
            currentWeight = currentWeight,
            previousWeight = previousWeight,
            weightChange = weightChange,
            waistFit = waistFit,
            units = units,
        )
    }

    suspend fun getNutritionInformation(
        start: LocalDateTime, end: LocalDateTime, units: Units
    ): Nutrition {
        if (!hasPermissions(setOf(NutritionRecord::class, HydrationRecord::class))) {
            return Nutrition(
                null, null, null, null, null, null, null, null, units
            )
        }

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

        val totalCalories =
            response.sumOf { it.result[NutritionRecord.ENERGY_TOTAL]?.inKilocalories ?: 0.0 }
        val averageCalories =
            if (response.isEmpty()) null
            else Energy.kilocalories(totalCalories / response.size)

        val totalProteinGrams =
            response.sumOf { it.result[NutritionRecord.PROTEIN_TOTAL]?.inGrams ?: 0.0 }
        val averageProteinGrams =
            if (response.isEmpty()) null
            else Mass.grams(totalProteinGrams / response.size)

        val totalCarbsGrams =
            response.sumOf { it.result[NutritionRecord.TOTAL_CARBOHYDRATE_TOTAL]?.inGrams ?: 0.0 }
        val averageCarbsGrams =
            if (response.isEmpty()) null
            else Mass.grams(totalCarbsGrams / response.size)

        val totalFatGrams =
            response.sumOf { it.result[NutritionRecord.TOTAL_FAT_TOTAL]?.inGrams ?: 0.0 }
        val averageFatGrams =
            if (response.isEmpty()) null
            else Mass.grams(totalFatGrams / response.size)

        val totalWaterLitres =
            response.sumOf { it.result[HydrationRecord.VOLUME_TOTAL]?.inLiters ?: 0.0 }
        val averageWaterLitres =
            if (response.isEmpty()) null
            else Volume.liters(totalWaterLitres / response.size)

        val daysLogged = response.size
        // TODO: calculate these values
        val daysWithinCalorieGoal = 0
        val daysProteinGoalMet = 0

        return Nutrition(
            averageCalories = averageCalories,
            averageProtein = averageProteinGrams,
            averageCarbs = averageCarbsGrams,
            averageFat = averageFatGrams,
            averageWater = averageWaterLitres,
            daysLogged = daysLogged,
            daysWithinCalorieGoal = daysWithinCalorieGoal,
            daysProteinGoalMet = daysProteinGoalMet,
            units = units,
        )
    }

    suspend fun getActivityInformation(
        start: LocalDateTime,
        end: LocalDateTime,
        units: Units
    ): Activity {
        if (!hasPermissions(
                setOf(
                    StepsRecord::class,
                    ActiveCaloriesBurnedRecord::class,
                    ExerciseSessionRecord::class
                )
            )
        ) {
            return Activity(null, null, null, null, null, units)
        }

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
        val activeCalories = Energy.kilocalories(response.sumOf {
            it.result[ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL]?.inKilocalories ?: 0.0
        })
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
            units = units,
        )
    }

    suspend fun getRecoveryInformation(start: LocalDateTime, end: LocalDateTime): Recovery {
        if (!hasPermissions(
                setOf(
                    SleepSessionRecord::class,
                    HeartRateRecord::class
                )
            )
        ) return Recovery(null, null)

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

    private suspend fun hasPermissions(permissions: Set<KClass<out Record>>): Boolean {
        for (permission in permissions) {
            val healthPermission = HealthPermission.getReadPermission(permission)
            if (!healthConnectPermissions.hasPermission(healthPermission)) {
                return false
            }
        }
        return true
    }
}
