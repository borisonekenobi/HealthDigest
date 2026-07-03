package com.borisonekenobi.healthdigest.model

import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.Volume

fun convertBig(value: Mass?, units: Units): String {
    return if (value == null) "N/A"
    else when (units) {
        Units.METRIC -> "%.1f kg".format(value.inKilograms)
        Units.IMPERIAL -> "%.1f lbs".format(value.inPounds)
    }
}

fun convertSmall(value: Mass?, units: Units): String {
    return if (value == null) "N/A"
    else when (units) {
        Units.METRIC -> "%.0f g".format(value.inGrams)
        Units.IMPERIAL -> "%.0f oz".format(value.inOunces)
    }
}

fun convert(value: Energy?, units: Units): String {
    return if (value == null) "N/A"
    else when (units) {
        Units.METRIC -> "%.0f kcal".format(value.inKilocalories)
        Units.IMPERIAL -> "%.0f kcal".format(value.inKilocalories)
    }
}

fun convert(value: Volume?, units: Units): String {
    return if (value == null) "N/A"
    else when (units) {
        Units.METRIC -> "%.1f L".format(value.inLiters)
        Units.IMPERIAL -> "%.1f fl oz".format(value.inFluidOuncesUs)
    }
}
