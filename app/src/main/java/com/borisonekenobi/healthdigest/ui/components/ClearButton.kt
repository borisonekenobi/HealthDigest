package com.borisonekenobi.healthdigest.ui.components

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.borisonekenobi.healthdigest.R

@Composable
fun ClearButton(enabled: Boolean, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
    ) {
        Text(stringResource(R.string.clear))
    }
}
