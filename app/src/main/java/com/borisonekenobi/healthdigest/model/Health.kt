package com.borisonekenobi.healthdigest.model

data class Health(
    val painOrInjury: Boolean,
    val illness: Boolean,
    val healthNotes: String = ""
) {
    override fun toString(): String {
        return """
            Pain or Injury: ${if (painOrInjury) "Yes" else "No"}
            Illness: ${if (illness) "Yes" else "No"}
            Health Notes:
${healthNotes.ifBlank { "N/A" }.prependIndent("            ")}"""
    }
}
