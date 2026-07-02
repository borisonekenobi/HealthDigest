package com.borisonekenobi.healthdigest.data

import com.borisonekenobi.healthdigest.model.UserData
import com.borisonekenobi.healthdigest.model.WeeklyReport

interface HealthRepository {
    suspend fun getWeeklyReport(userData: UserData): WeeklyReport
}
