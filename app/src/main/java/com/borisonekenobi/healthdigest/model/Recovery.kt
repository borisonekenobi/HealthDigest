package com.borisonekenobi.healthdigest.model

import java.time.Duration

data class Recovery(
    val averageSleep: Duration?,
    val averageHeartRate: Long?,
) {
    override fun toString(): String {
        return """
            Average Sleep: ${averageSleep?.toString() ?: "N/A"}
            Average Heart Rate: ${averageHeartRate ?: "N/A"} bpm"""
    }
}
