package com.alfie.whitepaper.ui.screen.home.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alfie.whitepaper.R


@Composable
fun DrawAppPreferenceRow(
    onClickSettings: () -> Unit,
    onClickShare: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 12.dp, end = 12.dp, top = 12.dp, bottom = 12.dp
            ), horizontalArrangement = Arrangement.End
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_settings),
            contentDescription = stringResource(id = R.string.str_preferences),
            modifier = Modifier
                .padding(end = 8.dp)
                .size(30.dp)
                .clickable {
                    onClickSettings.invoke()
                }
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_share),
            contentDescription = stringResource(id = R.string.str_preferences),
            modifier = Modifier
                .padding(start = 8.dp)
                .size(30.dp)
                .clickable {
                    onClickShare.invoke()
                }
        )
    }
}