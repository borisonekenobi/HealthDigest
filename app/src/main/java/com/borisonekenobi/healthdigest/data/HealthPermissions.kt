package com.borisonekenobi.healthdigest.data

import androidx.activity.result.ActivityResultLauncher

interface HealthPermissions {
    fun getPermissions(launcher: ActivityResultLauncher<Set<String>>)
    suspend fun hasPermission(healthPermission: String): Boolean
    suspend fun checkPermissions(): Set<String>
}
