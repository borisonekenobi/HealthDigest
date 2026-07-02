package com.borisonekenobi.healthdigest.model

data class BodyMetrics(
    val currentWeightKg: Double?,
    val previousWeightKg: Double?,
    val weightChangeKg: Double?,
    val waistFit: WaistFit?,
) {
    override fun toString(): String {
        return """
            Current Weight: ${currentWeightKg ?: "N/A"} kg
            Previous Weight: ${previousWeightKg ?: "N/A"} kg
            Weight Change: ${weightChangeKg ?: "N/A"} kg
            Clothes/Waist Fit: ${waistFit?.name ?: "N/A"}"""
    }
}

enum class WaistFit {
    MUCH_TIGHTER, TIGHTER, SAME, LOOSER, MUCH_LOOSER,
}
