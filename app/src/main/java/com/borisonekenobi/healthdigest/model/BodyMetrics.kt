package com.borisonekenobi.healthdigest.model

import androidx.health.connect.client.units.Mass

data class BodyMetrics(
    val currentWeight: Mass?,
    val previousWeight: Mass?,
    val weightChange: Mass?,
    val waistFit: WaistFit?,
    val units: Units,
) {
    override fun toString(): String {
        return """
            Current Weight: ${convertBig(currentWeight, units)}
            Previous Weight: ${convertBig(previousWeight, units)}
            Weight Change: ${convertBig(weightChange, units)}
            Clothes/Waist Fit: ${waistFit?.name ?: "N/A"}"""
    }
}

enum class WaistFit {
    MUCH_TIGHTER, TIGHTER, SAME, LOOSER, MUCH_LOOSER,
}
