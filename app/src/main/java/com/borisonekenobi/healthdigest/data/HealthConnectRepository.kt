package com.borisonekenobi.healthdigest.data

import android.content.Context
import com.borisonekenobi.healthdigest.model.GoalSummary
import com.borisonekenobi.healthdigest.model.Health
import com.borisonekenobi.healthdigest.model.Summary
import com.borisonekenobi.healthdigest.model.UserData
import com.borisonekenobi.healthdigest.model.WeeklyReport
import kotlinx.coroutines.flow.first

class HealthConnectRepository(private val context: Context) : HealthRepository {
    private val dataStoreSource = DataStoreSource(context)

    override suspend fun getWeeklyReport(userData: UserData): WeeklyReport {
        val userPreferences = dataStoreSource.userPreferencesFlow.first()
        val healthConnectSource = HealthConnectSource(context, userPreferences.units)

        return WeeklyReport(
            startMessage = userPreferences.messages.startMessage,
            summary = Summary(
                userData.hungerLevel,
                userData.hungerLevelComments,
                userData.energyLevel,
                userData.energyLevelComments
            ),
            body = healthConnectSource.getBodyInformation(userData.waistFit),
            nutrition = healthConnectSource.getNutritionInformation(userPreferences.goals),
            activity = healthConnectSource.getActivityInformation(),
            recovery = healthConnectSource.getRecoveryInformation(),
            health = Health(userData.painOrInjury, userData.illness, userData.healthNotes),
            goalSummary = GoalSummary(userPreferences.goals, userPreferences.units),
            notes = userData.notes,
            endMessage = userPreferences.messages.endMessage
        )
    }
}
