package com.borisonekenobi.healthdigest.model.settings

import java.time.LocalDate

data class PersonalInformation(
    var sex: Sex?,
    var birthDate: LocalDate?,
)

enum class Sex {
    MALE,
    FEMALE,
}

enum class ActivityLevel(val value: Double) {
    SEDENTARY(1.2),
    LIGHT(1.375),
    MODERATE(1.55),
    ACTIVE(1.725),
    VERY_ACTIVE(1.9),
}
