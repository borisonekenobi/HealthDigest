package com.borisonekenobi.healthdigest.data

import com.borisonekenobi.healthdigest.model.WeeklyReport

class FakeHealthRepository : HealthRepository {
    override suspend fun getWeeklyReport(notes: String): WeeklyReport {
        return WeeklyReport(
            calories = 2100,
            protein = 160,
            steps = 82000,
            weight = 100.6,
            notes = notes.ifBlank { "No notes provided." })
    }
}
