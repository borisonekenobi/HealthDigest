package com.borisonekenobi.healthdigest

import android.content.Context
import androidx.health.connect.client.HealthConnectClient

class HealthConnectManager(context: Context) {
    val client by lazy { HealthConnectClient.getOrCreate(context) }
}
