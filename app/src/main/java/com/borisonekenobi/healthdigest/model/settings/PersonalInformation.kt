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
