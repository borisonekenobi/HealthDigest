package com.borisonekenobi.healthdigest.model

data class WeeklyReport(
    val calories: Int,
    val protein: Int,
    val steps: Int,
    val weight: Double,
    val notes: String,
)

fun WeeklyReport.toDisplayString(): String {
    return """
WEEKLY REPORT

Calories: $calories
Protein: $protein
Steps: $steps
Weight: $weight

Notes:
$notes
""".trimIndent()
}
