package com.borisonekenobi.healthdigest.model

import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.Volume

fun convertBig(value: Mass?, units: Units): String {
    return if (value == null) "N/A"
    else when (units) {
        Units.METRIC -> "%.1f %s".format(value.inKilograms, bigMassUnits(units))
        Units.IMPERIAL -> "%.1f %s".format(value.inPounds, bigMassUnits(units))
    }
}

fun convertSmall(value: Mass?, units: Units): String {
    return if (value == null) "N/A"
    else when (units) {
        Units.METRIC -> "%.0f %s".format(value.inGrams, smallMassUnits(units))
        Units.IMPERIAL -> "%.0f %s".format(value.inOunces, smallMassUnits(units))
    }
}

fun convert(value: Energy?): String {
    return if (value == null) "N/A"
    else "%.0f %s".format(value.inKilocalories, energyUnits())
}

fun convert(value: Volume?, units: Units): String {
    return if (value == null) "N/A"
    else when (units) {
        Units.METRIC -> "%.2f %s".format(value.inMilliliters, volumeUnits(units))
        Units.IMPERIAL -> "%.2f %s".format(value.inFluidOuncesUs, volumeUnits(units))
    }
}

fun bigMassUnits(units: Units): String {
    return when (units) {
        Units.METRIC -> "kg"
        Units.IMPERIAL -> "lbs"
    }
}

fun smallMassUnits(units: Units): String {
    return when (units) {
        Units.METRIC -> "g"
        Units.IMPERIAL -> "oz"
    }
}

fun energyUnits(): String {
    return "kcal"
}

fun volumeUnits(units: Units): String {
    return when (units) {
        Units.METRIC -> "mL"
        Units.IMPERIAL -> "fl oz"
    }
}
