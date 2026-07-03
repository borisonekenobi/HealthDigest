package com.borisonekenobi.healthdigest.model

data class UserData(
    var hungerLevel: HungerLevel,
    var hungerLevelComments: String,
    var energyLevel: EnergyLevel,
    var energyLevelComments: String,
    var waistFit: WaistFit,
    var painOrInjury: Boolean,
    var illness: Boolean,
    var healthNotes: String,
    var notes: String,
)
