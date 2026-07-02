package com.borisonekenobi.healthdigest.data

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.records.NutritionRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord

class HealthConnectPermissions(context: Context) : HealthPermissions {
    private val client = HealthConnectClient.getOrCreate(context)

    override fun getPermissions(launcher: ActivityResultLauncher<Set<String>>) {
        val permissions = setOf(
            HealthPermission.getReadPermission(WeightRecord::class),
            HealthPermission.getReadPermission(NutritionRecord::class),
            HealthPermission.getReadPermission(HydrationRecord::class),
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
            HealthPermission.getReadPermission(ExerciseSessionRecord::class),
            HealthPermission.getReadPermission(SleepSessionRecord::class),
            HealthPermission.getReadPermission(HeartRateRecord::class),
        )

        launcher.launch(permissions)
    }

    override suspend fun hasPermission(healthPermission: String): Boolean {
        return checkPermissions().contains(healthPermission)
    }

    override suspend fun checkPermissions(): Set<String> {
        return client.permissionController.getGrantedPermissions()
    }
}
