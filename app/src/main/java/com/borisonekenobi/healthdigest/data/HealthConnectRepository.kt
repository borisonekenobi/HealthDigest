package com.borisonekenobi.healthdigest.data

import android.content.Context
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.kilocalories
import com.borisonekenobi.healthdigest.HealthConnectManager
import com.borisonekenobi.healthdigest.model.GoalSummary
import com.borisonekenobi.healthdigest.model.Health
import com.borisonekenobi.healthdigest.model.Summary
import com.borisonekenobi.healthdigest.model.UserData
import com.borisonekenobi.healthdigest.model.WeeklyReport
import com.borisonekenobi.healthdigest.model.settings.ActivityLevel
import com.borisonekenobi.healthdigest.model.settings.Sex
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period

class HealthConnectRepository(private val context: Context) : HealthRepository {
    private val dataStoreSource = DataStoreSource(context)

    override suspend fun getWeeklyReport(userData: UserData): WeeklyReport {
        val userPreferences = dataStoreSource.userPreferencesFlow.first()
        val healthConnectSource = HealthConnectSource(context, userPreferences.systemPreferences.units)

        val goalPreferences = userPreferences.goalPreferences
        if (goalPreferences.autoNutritionGoals) {
            val healthConnectManager = HealthConnectManager(context)
            val healthConnectPermissions = HealthConnectPermissions(context)
            var weight: Mass? = null
            var height: Length? = null
            
            try {
                val granted = healthConnectPermissions.checkPermissions()
                if (granted.contains(HealthPermission.getReadPermission(WeightRecord::class))) {
                    val weightResponse = healthConnectManager.client.readRecords(
                        ReadRecordsRequest(
                            recordType = WeightRecord::class,
                            timeRangeFilter = TimeRangeFilter.between(LocalDateTime.now().minusYears(5), LocalDateTime.now())
                        )
                    )
                    weight = weightResponse.records.lastOrNull()?.weight
                }
                if (granted.contains(HealthPermission.getReadPermission(HeightRecord::class))) {
                    val heightResponse = healthConnectManager.client.readRecords(
                        androidx.health.connect.client.request.ReadRecordsRequest(
                            recordType = HeightRecord::class,
                            timeRangeFilter = TimeRangeFilter.between(LocalDateTime.now().minusYears(5), LocalDateTime.now())
                        )
                    )
                    height = heightResponse.records.lastOrNull()?.height
                }
            } catch (_: Exception) {}
            
            val w = weight ?: Mass.kilograms(70.0)
            val h = height ?: Length.meters(1.75)
            val s = userPreferences.personalInformation.sex ?: Sex.MALE
            val age = userPreferences.personalInformation.birthDate?.let { Period.between(it, LocalDate.now()).years } ?: 30
            val al = ActivityLevel.MODERATE
            
            val energyRange = recommendedEnergy(s, w, h, age, al, goalPreferences.weightGoal)
            val proteinRangeObj = recommendedProtein(w)
            
            val basalMetabolicRate = when (s) {
                Sex.MALE -> 10 * w.inKilograms + 6.25 * (h.inMeters * 100) - 5 * age + 5
                Sex.FEMALE -> 10 * w.inKilograms + 6.25 * (h.inMeters * 100) - 5 * age - 161
            }
            val totalDailyEnergyExpenditure = basalMetabolicRate * al.value
            val targetEnergy = (totalDailyEnergyExpenditure + goalPreferences.weightGoal.value.inKilocalories).kilocalories
            
            val fatRangeObj = recommendedFat(targetEnergy)
            val carbsRangeObj = recommendedCarbs(targetEnergy, proteinRangeObj, fatRangeObj)
            
            goalPreferences.calorieGoal = energyRange
            goalPreferences.proteinGoal = proteinRangeObj
            goalPreferences.carbsGoal = carbsRangeObj
            goalPreferences.fatGoal = fatRangeObj
        }

        if (goalPreferences.autoHydrationGoals) {
            val sex = userPreferences.personalInformation.sex ?: Sex.MALE
            goalPreferences.waterGoal = recommendedWater(sex)
        }

        return WeeklyReport(
            startMessage = userPreferences.reportPreferences.startMessage,
            summary = Summary(
                userData.hungerLevel,
                userData.hungerLevelComments,
                userData.energyLevel,
                userData.energyLevelComments
            ),
            body = healthConnectSource.getBodyInformation(userData.waistFit),
            nutrition = healthConnectSource.getNutritionInformation(userPreferences.goalPreferences),
            activity = healthConnectSource.getActivityInformation(),
            recovery = healthConnectSource.getRecoveryInformation(),
            health = Health(userData.painOrInjury, userData.illness, userData.healthNotes),
            goalSummary = GoalSummary(userPreferences.goalPreferences, userPreferences.systemPreferences.units),
            notes = userData.notes,
            endMessage = userPreferences.reportPreferences.endMessage
        )
    }
}
