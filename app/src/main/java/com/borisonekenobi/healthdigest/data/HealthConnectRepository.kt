package com.borisonekenobi.healthdigest.data

import android.content.Context
import com.borisonekenobi.healthdigest.model.Health
import com.borisonekenobi.healthdigest.model.Summary
import com.borisonekenobi.healthdigest.model.UserData
import com.borisonekenobi.healthdigest.model.WeeklyReport
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime

class HealthConnectRepository(context: Context) : HealthRepository {
    private val healthConnectSource = HealthConnectSource(context)
    private val dataStoreSource = DataStoreSource(context)

    override suspend fun getWeeklyReport(userData: UserData): WeeklyReport {
        val userPreferences = dataStoreSource.userPreferencesFlow.first()
        val end = LocalDateTime.now()
        val start = end.minusDays(7)

        return WeeklyReport(
            startMessage = userPreferences.startMessage,
            summary = Summary(userData.hungerLevel, userData.hungerLevelComments, userData.energyLevel, userData.energyLevelComments),
            body = healthConnectSource.getBodyInformation(start, end, userData.waistFit, userPreferences.units),
            nutrition = healthConnectSource.getNutritionInformation(start, end, userPreferences.units),
            activity = healthConnectSource.getActivityInformation(start, end, userPreferences.units),
            recovery = healthConnectSource.getRecoveryInformation(start, end),
            health = Health(userData.painOrInjury, userData.illness, userData.healthNotes),
            notes = userData.notes,
            endMessage = userPreferences.endMessage
        )
    }
}
