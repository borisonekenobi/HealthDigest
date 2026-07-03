package com.borisonekenobi.healthdigest.model

import android.annotation.SuppressLint
import java.time.Duration
import kotlin.time.toKotlinDuration

data class Recovery(
    val averageSleep: Duration?,
    val averageHeartRate: Long?,
) {
    @SuppressLint("DefaultLocale")
    override fun toString(): String {
        val averageSleep = averageSleep?.toKotlinDuration()?.toComponents { hours, minutes, _, _ ->
            String.format("%dh%dm", hours, minutes)
        }

        return """
            Average Sleep: ${averageSleep ?: "N/A"}
            Average Heart Rate: ${averageHeartRate ?: "N/A"} bpm"""
    }
}
