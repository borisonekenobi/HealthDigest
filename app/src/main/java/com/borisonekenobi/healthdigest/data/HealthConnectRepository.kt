package com.borisonekenobi.healthdigest.data

import android.content.Context
import com.borisonekenobi.healthdigest.model.Health
import com.borisonekenobi.healthdigest.model.Summary
import com.borisonekenobi.healthdigest.model.UserData
import com.borisonekenobi.healthdigest.model.WeeklyReport
import java.time.LocalDateTime

class HealthConnectRepository(context: Context) : HealthRepository {
    private val healthConnectSource = HealthConnectSource(context)

    override suspend fun getWeeklyReport(userData: UserData): WeeklyReport {
        val end = LocalDateTime.now()
        val start = end.minusDays(7)

        return WeeklyReport(
            summary = Summary(userData.hungerLevel, userData.energyLevel, userData.additionalComments),
            body = healthConnectSource.getBodyInformation(start, end, userData.waistFit),
            nutrition = healthConnectSource.getNutritionInformation(start, end),
            activity = healthConnectSource.getActivityInformation(start, end),
            recovery = healthConnectSource.getRecoveryInformation(start, end),
            health = Health(userData.painOrInjury, userData.illness, userData.healthNotes),
            notes = userData.notes,
        )
    }
}
