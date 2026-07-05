package com.borisonekenobi.healthdigest.model

import android.annotation.SuppressLint
import kotlin.time.Duration

data class Recovery(
    val averageSleep: Duration?,
    val averageHeartRate: Long?,
) {
    @SuppressLint("DefaultLocale")
    override fun toString(): String {
        val averageSleep = averageSleep?.toComponents { hours, minutes, _, _ ->
            String.format("%dh%dm", hours, minutes)
        }

        return """
            Average Sleep: ${averageSleep ?: "N/A"}
            Average Heart Rate: ${averageHeartRate ?: "N/A"} bpm"""
    }
}
