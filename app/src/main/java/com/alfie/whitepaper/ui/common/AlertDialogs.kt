package com.alfie.whitepaper.ui.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.alfie.whitepaper.R
import com.alfie.whitepaper.ui.theme.montserratFamily


@Composable
fun DrawAlertDialog(
    @DrawableRes iconImageRes: Int = R.drawable.ic_alert_squre,
    @StringRes title: Int,
    @StringRes description: Int,
    isOpened: MutableState<Boolean>,
    onClickPositiveButton: () -> Unit,
    onClickNegativeButton: () -> Unit
) {
    Dialog(onDismissRequest = { isOpened.value = false }) {
        DrawAlertDialogUI(
            isOpened = isOpened,
            iconImageRes = iconImageRes,
            title = title,
            description = description,
            onClickPositiveButton = onClickPositiveButton,
            onClickNegativeButton = onClickNegativeButton
        )
    }
}

@Composable
fun DrawAlertDialogUI(
    modifier: Modifier = Modifier,
    @DrawableRes iconImageRes: Int,
    @StringRes title: Int,
    @StringRes description: Int,
    isOpened: MutableState<Boolean>,
    onClickPositiveButton: () -> Unit,
    onClickNegativeButton: () -> Unit

) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(10.dp, 5.dp, 10.dp, 10.dp),
    ) {
        Column(
            modifier
                .background(MaterialTheme.colorScheme.onSecondary)
        ) {

            Image(
                painter = painterResource(id = iconImageRes),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .padding(top = 35.dp)
                    .height(70.dp)
                    .fillMaxWidth(),

                )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = title),
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    fontFamily = montserratFamily,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(id = description),
                    modifier = Modifier
                        .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    fontFamily = montserratFamily,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 20.dp, bottom = 22.dp,
                        start = 16.dp, end = 16.dp
                    ),
                horizontalArrangement = Arrangement.SpaceAround
            ) {

                val noContentDesc = stringResource(id = R.string.str_no)
                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12),
                    onClick = {
                        isOpened.value = false
                        onClickNegativeButton.invoke()
                    }) {
                    Text(
                        text = noContentDesc,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = montserratFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
                val yesContentDesc = stringResource(id = R.string.str_yes)
                Button(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .weight(1f)
                        .semantics {
                            contentDescription = yesContentDesc
                        },
                    shape = RoundedCornerShape(12),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    onClick = {
                        isOpened.value = false
                        onClickPositiveButton.invoke()
                    }) {
                    Text(
                        text = yesContentDesc,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontFamily = montserratFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}