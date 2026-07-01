package com.borisonekenobi.healthdigest.data

import com.borisonekenobi.healthdigest.model.WeeklyReport

interface HealthRepository {
    suspend fun getWeeklyReport(notes: String): WeeklyReport
}
