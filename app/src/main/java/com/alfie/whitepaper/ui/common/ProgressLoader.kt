package com.alfie.whitepaper.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProgressLoader(
    showProgressBar: Boolean
) {
    if (showProgressBar) {
        Box(modifier = Modifier
            .fillMaxSize()
            .clickable(enabled = !showProgressBar) {
                // Disable interaction when progress bar is visible
            }) {
            CircularProgressIndicator(
                // Or LinearProgressIndicator
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.Center)
            )
        }
    }
}