package com.borisonekenobi.healthdigest.model.settings

data class UserPreferences(
    val personalInformation: PersonalInformation,
    val goalPreferences: GoalPreferences,
    val reportPreferences: ReportPreferences,
    val systemPreferences: SystemPreferences,
)
