package com.borisonekenobi.healthdigest.model

data class WeeklyReport(
    val summary: Summary,
    val body: BodyMetrics,
    val nutrition: Nutrition,
    val activity: Activity,
    val recovery: Recovery,
    val health: Health,
    val notes: String
) {
    override fun toString(): String {
        return """
            # Weekly Report
            
            ## Summary
            $summary
            
            ## Body Metrics
            $body
            
            ## Nutrition
            $nutrition
            
            ## Activity
            $activity
            
            ## Recovery
            $recovery
            
            ## Health
            $health
            
            ## Notes
${notes.ifBlank { "N/A" }.prependIndent("            ")}
        """.trimIndent()
    }
}
