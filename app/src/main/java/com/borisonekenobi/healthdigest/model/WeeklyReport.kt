package com.borisonekenobi.healthdigest.model

data class WeeklyReport(
    val startMessage: String,
    val summary: Summary,
    val body: BodyMetrics,
    val nutrition: Nutrition,
    val activity: Activity,
    val recovery: Recovery,
    val health: Health,
    val notes: String,
    val endMessage: String,
) {
    override fun toString(): String {
        val sm =if (startMessage == "") ""
        else "$startMessage\n            \n            "

        val em = if (endMessage == "") ""
        else "\n            \n            $endMessage"

        return """
            $sm# Weekly Report
            
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
${notes.ifBlank { "N/A" }.prependIndent("            ")}$em
        """.trimIndent()
    }
}
