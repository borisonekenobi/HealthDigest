package com.borisonekenobi.healthdigest.model

data class UserData(
    var hungerLevel: HungerLevel,
    var energyLevel: EnergyLevel,
    var additionalComments: String,
    var waistFit: WaistFit,
    var painOrInjury: Boolean,
    var illness: Boolean,
    var healthNotes: String,
    var notes: String,
)
