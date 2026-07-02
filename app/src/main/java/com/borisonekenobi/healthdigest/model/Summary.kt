package com.borisonekenobi.healthdigest.model

data class Summary(
    var hungerLevel: HungerLevel,
    val energyLevel: EnergyLevel,
    val additionalComments: String = ""
) {
    override fun toString(): String {
        return """
            Hunger Level: ${hungerLevel.name}
            Energy Level: ${energyLevel.name}
            Additional Comments:
${additionalComments.ifBlank { "N/A" }.prependIndent("            ")}"""
    }
}

enum class HungerLevel {
    VERY_HIGH, HIGH, OK, GOOD, GREAT,
}

enum class EnergyLevel {
    VERY_LOW, LOW, OK, GOOD, GREAT,
}
