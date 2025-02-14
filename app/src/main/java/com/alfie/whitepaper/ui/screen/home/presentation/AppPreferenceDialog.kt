package com.alfie.whitepaper.ui.screen.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.alfie.whitepaper.R
import com.alfie.whitepaper.data.constants.BY_SYSTEM
import com.alfie.whitepaper.data.constants.DARK_MODE
import com.alfie.whitepaper.data.constants.LIGHT_MODE
import com.alfie.whitepaper.ui.screen.home.state.HomeUserEvents
import com.alfie.whitepaper.ui.screen.home.state.HomeUserState
import com.alfie.whitepaper.ui.theme.montserratFamily

@Composable
fun DrawAppPreferenceDialog(
    userState: HomeUserState,
    userEvent: HomeUserEvents,
    isOpened: MutableState<Boolean>,
) {
    Dialog(onDismissRequest = { isOpened.value = false }) {
        DrawAppPreferenceDialogUI(
            userState = userState, userEvent = userEvent
        )
    }
}

@Composable
private fun DrawAppPreferenceDialogUI(
    modifier: Modifier = Modifier,
    userState: HomeUserState,
    userEvent: HomeUserEvents,
) {

    val darkThemeOptions = listOf(DARK_MODE, LIGHT_MODE, BY_SYSTEM)

    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        ConstraintLayout(
            modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 22.dp, vertical = 12.dp)
        ) {
            val (
                preferenceTitle, dynamicThemeLabel, dynamicThemeSwitch, dynamicThemeDiv,
                darkThemeLabel, darkThemeOptionRow,
            ) = createRefs()

            Text(
                modifier = Modifier
                    .constrainAs(
                        preferenceTitle
                    ) {
                        top.linkTo(parent.top, 12.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end, 12.dp)
                    },
                text = stringResource(id = R.string.str_preferences),
                fontFamily = montserratFamily,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                modifier = Modifier
                    .constrainAs(
                        dynamicThemeLabel
                    ) {
                        top.linkTo(preferenceTitle.bottom, 30.dp)
                        start.linkTo(parent.start)
                        end.linkTo(dynamicThemeSwitch.start, 8.dp)
                        width = Dimension.fillToConstraints
                    },
                text = stringResource(id = R.string.str_dynamic_color),
                style = TextStyle(textAlign = TextAlign.Start),
                fontFamily = montserratFamily,
                fontWeight = FontWeight.Bold
            )
            DrawDynamicThemeSwitch(
                modifier = Modifier.constrainAs(dynamicThemeSwitch) {
                    top.linkTo(dynamicThemeLabel.top)
                    bottom.linkTo(dynamicThemeLabel.bottom)
                    start.linkTo(dynamicThemeLabel.end)
                    end.linkTo(parent.end)
                }, userState = userState, userEvent = userEvent
            )
            Divider(
                Modifier
                    .constrainAs(dynamicThemeDiv) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(dynamicThemeSwitch.bottom, 12.dp)
                    }
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outline)
                    .fillMaxWidth())
            Text(
                modifier = Modifier
                    .constrainAs(
                        darkThemeLabel
                    ) {
                        top.linkTo(dynamicThemeDiv.bottom, 12.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    },
                text = stringResource(id = R.string.str_dark_theme),
                style = TextStyle(textAlign = TextAlign.Start),
                fontFamily = montserratFamily,
                fontWeight = FontWeight.Bold
            )

            Row(modifier = Modifier.constrainAs(darkThemeOptionRow) {
                top.linkTo(darkThemeLabel.bottom, 12.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {
                darkThemeOptions.forEach { option ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (option == userState.isEnableDarkTheme.value),
                            onClick = {
                                userEvent.onDarkThemeChange.invoke(option)
                                userState.isEnableDarkTheme.value = option
                            })
                        Text(
                            text = stringResource(id = userState.getDarkThemeOptionName(option)),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 1.dp),
                            fontSize = 12.sp,
                            fontFamily = montserratFamily,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawDynamicThemeSwitch(
    modifier: Modifier = Modifier,
    userState: HomeUserState,
    userEvent: HomeUserEvents
) {
    val dynamicColorContentDesc = stringResource(id = R.string.str_dynamic_color)

    Switch(
        modifier = modifier.semantics {
            contentDescription = dynamicColorContentDesc
            role = Role.Switch
        },
        checked = userState.isEnableDynamicTheme.value,
        onCheckedChange = {
            userEvent.onDynamicThemeChange.invoke(it)
            userState.isEnableDynamicTheme.value = it
        }, colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.primary,
            checkedTrackColor = Color.White.copy(alpha = .5f),
            checkedBorderColor = Color.DarkGray,
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = Color.White.copy(alpha = .5f),
            uncheckedBorderColor = Color.DarkGray
        )
    )
}