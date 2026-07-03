package com.borisonekenobi.healthdigest.model

data class Summary(
    val hungerLevel: HungerLevel,
    val hungerLevelComments: String = "",
    val energyLevel: EnergyLevel,
    val energyLevelComments: String = ""
) {
    override fun toString(): String {
        return """
            Hunger Level: ${hungerLevel.name}
            Comments:
${hungerLevelComments.ifBlank { "N/A" }.prependIndent("            ")}
            Energy Level: ${energyLevel.name}
            Comments:
${energyLevelComments.ifBlank { "N/A" }.prependIndent("            ")}"""
    }
}

enum class HungerLevel {
    VERY_HIGH, HIGH, OK, GOOD, GREAT,
}

enum class EnergyLevel {
    VERY_LOW, LOW, OK, GOOD, GREAT,
}
