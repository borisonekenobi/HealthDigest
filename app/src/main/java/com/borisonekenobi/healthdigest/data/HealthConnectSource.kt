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
import androidx.health.connect.client.units.grams
import androidx.health.connect.client.units.kilocalories
import androidx.health.connect.client.units.kilograms
import androidx.health.connect.client.units.milliliters
import com.borisonekenobi.healthdigest.HealthConnectManager
import com.borisonekenobi.healthdigest.model.Activity
import com.borisonekenobi.healthdigest.model.BodyMetrics
import com.borisonekenobi.healthdigest.model.Nutrition
import com.borisonekenobi.healthdigest.model.Recovery
import com.borisonekenobi.healthdigest.model.WaistFit
import com.borisonekenobi.healthdigest.model.settings.GoalPreferences
import com.borisonekenobi.healthdigest.model.settings.Units
import com.borisonekenobi.healthdigest.model.settings.inRange
import java.time.LocalDateTime
import java.time.Period
import kotlin.reflect.KClass
import kotlin.time.Duration.Companion.minutes

class HealthConnectSource(context: Context, private val units: Units) {
    private val healthConnectManager = HealthConnectManager(context)
    private val healthConnectPermissions = HealthConnectPermissions(context)

    private val now = LocalDateTime.now()
    private val today = now.withHour(0).withMinute(0).withSecond(0).withNano(0)
    private val lastWeek = today.minusDays(7)
    private val lastLastWeek = today.minusDays(14)

    suspend fun getBodyInformation(waistFit: WaistFit): BodyMetrics {
        if (!hasPermissions(setOf(WeightRecord::class))) {
            return BodyMetrics(
                null, null, null, null, units
            )
        }

        var response = healthConnectManager.client.aggregateGroupByPeriod(
            AggregateGroupByPeriodRequest(
                metrics = setOf(WeightRecord.WEIGHT_AVG),
                timeRangeFilter = TimeRangeFilter.after(today),
                timeRangeSlicer = Period.ofDays(1),
            )
        )

        val currentWeight = response.lastOrNull()?.result?.get(WeightRecord.WEIGHT_AVG)

        response = healthConnectManager.client.aggregateGroupByPeriod(
            AggregateGroupByPeriodRequest(
                metrics = setOf(WeightRecord.WEIGHT_AVG),
                timeRangeFilter = TimeRangeFilter.between(lastLastWeek, today),
                timeRangeSlicer = Period.ofDays(1),
            )
        )

        val previousWeight = response.lastOrNull()?.result?.get(WeightRecord.WEIGHT_AVG)
        val weightChange = if (currentWeight == null || previousWeight == null) null
        else (currentWeight.inKilograms - previousWeight.inKilograms).kilograms

        return BodyMetrics(
            currentWeight = currentWeight,
            previousWeight = previousWeight,
            weightChange = weightChange,
            waistFit = waistFit,
            units = units,
        )
    }

    suspend fun getNutritionInformation(userGoals: GoalPreferences): Nutrition {
        if (!hasPermissions(setOf(NutritionRecord::class, HydrationRecord::class))) {
            return Nutrition(
                null, null, null, null, null, 0, null, null, null, null, null, units
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
                timeRangeFilter = TimeRangeFilter.between(lastWeek, today),
                timeRangeSlicer = Period.ofDays(1),
            )
        )

        val totalCalories =
            response.sumOf { it.result[NutritionRecord.ENERGY_TOTAL]?.inKilocalories ?: 0.0 }
        val averageCalories = if (response.isEmpty()) null
        else (totalCalories / response.size).kilocalories

        val totalProteinGrams =
            response.sumOf { it.result[NutritionRecord.PROTEIN_TOTAL]?.inGrams ?: 0.0 }
        val averageProteinGrams = if (response.isEmpty()) null
        else (totalProteinGrams / response.size).grams

        val totalCarbsGrams =
            response.sumOf { it.result[NutritionRecord.TOTAL_CARBOHYDRATE_TOTAL]?.inGrams ?: 0.0 }
        val averageCarbsGrams = if (response.isEmpty()) null
        else (totalCarbsGrams / response.size).grams

        val totalFatGrams =
            response.sumOf { it.result[NutritionRecord.TOTAL_FAT_TOTAL]?.inGrams ?: 0.0 }
        val averageFatGrams = if (response.isEmpty()) null
        else (totalFatGrams / response.size).grams

        val totalWaterMilliliters =
            response.sumOf { it.result[HydrationRecord.VOLUME_TOTAL]?.inMilliliters ?: 0.0 }
        val averageWaterMilliliters = if (response.isEmpty()) null
        else (totalWaterMilliliters / response.size).milliliters

        return Nutrition(
            averageCalories = averageCalories,
            averageProtein = averageProteinGrams,
            averageCarbs = averageCarbsGrams,
            averageFat = averageFatGrams,
            averageWater = averageWaterMilliliters,
            daysLogged = response.size,
            daysWithinCalorieGoal = if (userGoals.calorieGoal == null) null else response.count { item ->
                item.result[NutritionRecord.ENERGY_TOTAL]?.inRange(userGoals.calorieGoal!!) == true
            },
            daysProteinGoalMet = if (userGoals.proteinGoal == null) null else response.count { item ->
                item.result[NutritionRecord.PROTEIN_TOTAL]?.inRange(userGoals.proteinGoal!!) == true
            },
            daysCarbsGoalMet = if (userGoals.carbsGoal == null) null else response.count { item ->
                item.result[NutritionRecord.TOTAL_CARBOHYDRATE_TOTAL]?.inRange(userGoals.carbsGoal!!) == true
            },
            daysFatGoalMet = if (userGoals.fatGoal == null) null else response.count { item ->
                item.result[NutritionRecord.TOTAL_FAT_TOTAL]?.inRange(userGoals.fatGoal!!) == true
            },
            daysWaterGoalMet = if (userGoals.waterGoal == null) null else response.count { item ->
                (item.result[HydrationRecord.VOLUME_TOTAL] ?: 0.milliliters) >= userGoals.waterGoal!!
            },
            units = units,
        )
    }

    suspend fun getActivityInformation(): Activity {
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
                timeRangeFilter = TimeRangeFilter.between(lastWeek, today),
                timeRangeSlicer = Period.ofDays(1),
            )
        )

        val totalSteps = response.sumOf { it.result[StepsRecord.COUNT_TOTAL] ?: 0 }
        val averageStepsPerDay = if (response.isEmpty()) 0 else totalSteps / response.size
        val activeCalories = (response.sumOf {
            it.result[ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL]?.inKilocalories ?: 0.0
        }).kilocalories
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

    suspend fun getRecoveryInformation(): Recovery {
        if (!hasPermissions(
                setOf(
                    SleepSessionRecord::class, HeartRateRecord::class
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
                timeRangeFilter = TimeRangeFilter.between(lastWeek, today),
                timeRangeSlicer = Period.ofDays(1),
            )
        )

        val totalSleep = response.sumOf {
            it.result[SleepSessionRecord.SLEEP_DURATION_TOTAL]?.toMinutes() ?: 0
        }
        val averageSleep =
            if (response.isEmpty()) 0.minutes else (totalSleep / response.size).minutes
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
