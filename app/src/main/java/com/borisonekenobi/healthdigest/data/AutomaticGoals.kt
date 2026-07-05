package com.borisonekenobi.healthdigest.data

import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.Volume
import androidx.health.connect.client.units.grams
import androidx.health.connect.client.units.kilocalories
import androidx.health.connect.client.units.milliliters
import com.borisonekenobi.healthdigest.model.settings.ActivityLevel
import com.borisonekenobi.healthdigest.model.settings.Range
import com.borisonekenobi.healthdigest.model.settings.Sex
import com.borisonekenobi.healthdigest.model.settings.WeightGoal

fun recommendedEnergy(
    sex: Sex?,
    weight: Mass,
    height: Length,
    age: Int,
    activityLevel: ActivityLevel,
    weightGoal: WeightGoal
): Range<Energy>? {
    val basalMetabolicRate = when (sex) {
        Sex.MALE -> 10 * weight.inKilograms + 6.25 * (height.inMeters * 100) - 5 * age + 5
        Sex.FEMALE -> 10 * weight.inKilograms + 6.25 * (height.inMeters * 100) - 5 * age - 161
        else -> return null
    }

    val totalDailyEnergyExpenditure = basalMetabolicRate * activityLevel.value

    return Range(
        lowerBound = ((totalDailyEnergyExpenditure + weightGoal.value.inKilocalories) * 0.95).kilocalories,
        upperBound = ((totalDailyEnergyExpenditure + weightGoal.value.inKilocalories) * 1.05).kilocalories,
    )
}

fun recommendedProtein(weight: Mass): Range<Mass> {
    return Range(
        lowerBound = (weight.inKilograms * 1.2).grams,
        upperBound = (weight.inKilograms * 1.4).grams,
    )
}

fun recommendedFat(energy: Energy): Range<Mass> {
    return Range(
        lowerBound = (energy.inKilocalories * 0.2 / 9).grams,
        upperBound = (energy.inKilocalories * 0.35 / 9).grams,
    )
}

fun recommendedCarbs(energy: Energy, protein: Range<Mass>, fat: Range<Mass>): Range<Mass> {
    val lowerCalories = if (protein.upperBound == null || fat.upperBound == null) null
    else energy.inKilocalories - protein.upperBound!!.inGrams * 4 - fat.upperBound!!.inGrams * 9

    val upperCalories = if (protein.lowerBound == null || fat.lowerBound == null) null
    else energy.inKilocalories - protein.lowerBound!!.inGrams * 4 - fat.lowerBound!!.inGrams * 9

    return Range(
        lowerBound = if (lowerCalories == null) null else (lowerCalories / 4).grams,
        upperBound = if (upperCalories == null) null else (upperCalories / 4).grams,
    )
}

fun recommendedWater(sex: Sex?): Volume? {
    return when (sex) {
        Sex.MALE -> 3700.milliliters
        Sex.FEMALE -> 2700.milliliters
        else -> null
    }
}
