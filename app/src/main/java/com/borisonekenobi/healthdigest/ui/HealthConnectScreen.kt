package com.borisonekenobi.healthdigest.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.PermissionController
import com.borisonekenobi.healthdigest.R
import com.borisonekenobi.healthdigest.data.HealthConnectPermissions
import com.borisonekenobi.healthdigest.data.HealthPermissions
import kotlinx.coroutines.launch

@Composable
fun HealthConnectScreen(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val permission: HealthPermissions = remember { HealthConnectPermissions(context) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { _ -> }

    val allPermissionsGrantedMsg = stringResource(R.string.all_permissions_granted)
    val notAllPermissionsGrantedMsg = stringResource(R.string.not_all_permissions_granted)

    var allPermissionsGranted by remember { mutableStateOf<Boolean?>(null) }
    LaunchedEffect(Unit) {
        allPermissionsGranted = permission.hasAllPermissions()
    }

    suspend fun checkPermissions() {
        allPermissionsGranted = null
        allPermissionsGranted = permission.hasAllPermissions()
        Toast.makeText(
            context, when (allPermissionsGranted) {
                true -> allPermissionsGrantedMsg
                else -> notAllPermissionsGrantedMsg
            }, Toast.LENGTH_SHORT
        ).show()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(onClick = {
                permission.getPermissions(permissionLauncher)
                scope.launch { checkPermissions() }
            }) {
                Text(stringResource(R.string.connect_health_data))
            }

            if (allPermissionsGranted == null) {
                CircularProgressIndicator()
            } else {
                IconButton(
                    onClick = {
                        scope.launch { checkPermissions() }
                    }) {
                    Icon(
                        imageVector = when (allPermissionsGranted) {
                            true -> Icons.Default.CheckCircleOutline
                            else -> Icons.Default.ErrorOutline
                        }, contentDescription = stringResource(R.string.permission_status)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HealthConnectPreview() {
    HealthDigestApp("health-connect")
}
