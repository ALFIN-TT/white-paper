package com.alfie.whitepaper.ui.screen.home.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alfie.whitepaper.R
import com.alfie.whitepaper.core.utils.shareImageFromAssets
import com.alfie.whitepaper.core.utils.toast
import com.alfie.whitepaper.data.constants.Keys
import com.alfie.whitepaper.ui.common.DrawAlertDialog
import com.alfie.whitepaper.ui.common.ProgressLoader
import com.alfie.whitepaper.ui.common.addmob.AdmobBanner
import com.alfie.whitepaper.ui.navigation.Screen
import com.alfie.whitepaper.ui.screen.home.state.HomeUserEvents
import com.alfie.whitepaper.ui.screen.home.state.HomeUserState
import com.alfie.whitepaper.ui.screen.home.state.InitialHomeUserState


@Composable
fun HomeUI(
    navController: NavController, userState: HomeUserState, userEvent: HomeUserEvents
) {
    DrawRootView(
        navController = navController, userState = userState, userEvent = userEvent
    )
}

@Composable
private fun DrawRootView(
    navController: NavController, userState: HomeUserState, userEvent: HomeUserEvents
) {
    val isOpenedAppPreferenceDialog = rememberSaveable { mutableStateOf(false) }
    val isOpenedDeleteProjectDialog = rememberSaveable { mutableStateOf(false) }
    val selectedProjectName = rememberSaveable { mutableStateOf("") }

    Scaffold { it ->
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            DrawPreferenceDialog(
                userState = userState, userEvent = userEvent, isOpened = isOpenedAppPreferenceDialog
            )
            DrawDeleteProjectDialog(
                projectName = selectedProjectName,
                userState = userState,
                userEvent = userEvent,
                isOpened = isOpenedDeleteProjectDialog
            )
            DrawScreenBody(
                isOpenedAppPreferenceDialog = isOpenedAppPreferenceDialog,
                isOpenedDeleteProjectDialog = isOpenedDeleteProjectDialog,
                selectedProjectName = selectedProjectName,
                navController = navController,
                userState = userState,
                userEvent = userEvent
            )
        }
    }
}


@Composable
private fun DrawScreenBody(
    isOpenedAppPreferenceDialog: MutableState<Boolean>,
    isOpenedDeleteProjectDialog: MutableState<Boolean>,
    selectedProjectName: MutableState<String>,
    userState: HomeUserState,
    userEvent: HomeUserEvents,
    navController: NavController
) {
    val context = LocalContext.current
    LaunchedEffect(userState.initialState) {
        manageProjectImportFromDeepLink(
            initialHomeUserState = userState.initialState,
            context = context,
            userEvent = userEvent,
            navController = navController
        )
    }
    val startForResult = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        manageProjectImport(
            result = result,
            context = context,
            userEvent = userEvent,
            navController = navController
        )
    }
    ProgressLoader(showProgressBar = userState.getProjectsLoader)
    LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(
        start = 12.dp, top = 16.dp, end = 12.dp, bottom = 16.dp
    ), content = {
        item(span = { GridItemSpan(2) }) {
            DrawAppPreferenceRow(
                onClickSettings = {
                    isOpenedAppPreferenceDialog.value = true
                },
                onClickShare = {
                    shareImageFromAssets(
                        context = context,
                        sharePopupTitle = R.string.str_share,
                        textContent = R.string.str_share_app_desc
                    )
                }
            )
        }
        item(span = { GridItemSpan(1) }) {
            ProjectCard(
                label = R.string.str_new_project, icon = R.drawable.ic_new_project
            ) {
                navController.navigate(route = Screen.CanvasScreen.route.plus("?${Keys.PROJECT_NAME}=${""}"))
            }
        }
        item(span = { GridItemSpan(1) }) {
            ProjectCard(
                label = R.string.str_import, icon = R.drawable.ic_import
            ) {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "text/plain"
                    putExtra(
                        DocumentsContract.EXTRA_INITIAL_URI,
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    )
                }
                startForResult.launch(intent)
            }
        }
        items(
            span = { GridItemSpan(1) },
            count = userState.homeState.value.projects.size
        ) {
            DrawProjectCard(
                count = it,
                isOpenedDeleteProjectDialog = isOpenedDeleteProjectDialog,
                selectedProjectName = selectedProjectName,
                userState = userState,
                navController = navController
            )
        }
        item(span = { GridItemSpan(2) }) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp)
            )
        }
        item(span = { GridItemSpan(2) }) {
            AdmobBanner(
                modifier = Modifier.fillMaxWidth()
            )
        }
    })
}


@Composable
private fun DrawProjectCard(
    count: Int,
    isOpenedDeleteProjectDialog: MutableState<Boolean>,
    selectedProjectName: MutableState<String>,
    userState: HomeUserState,
    navController: NavController
) {
    val drawBoxPayLoad = userState.homeState.value.projects[count]
    ProjectCard(drawBoxPayLoad, onclick = {
        navController.navigate(route = Screen.CanvasScreen.route.plus("?${Keys.PROJECT_NAME}=${drawBoxPayLoad.name}"))
    }, onLongPress = {
        selectedProjectName.value = drawBoxPayLoad.name
        isOpenedDeleteProjectDialog.value = true
    })
}

@Composable
private fun DrawPreferenceDialog(
    isOpened: MutableState<Boolean>, userEvent: HomeUserEvents, userState: HomeUserState
) {
    if (isOpened.value) {
        DrawAppPreferenceDialog(
            isOpened = isOpened, userState = userState, userEvent = userEvent
        )
    }
}

@Composable
private fun DrawDeleteProjectDialog(
    projectName: MutableState<String>,
    isOpened: MutableState<Boolean>,
    userState: HomeUserState,
    userEvent: HomeUserEvents
) {
    if (isOpened.value) {
        DrawAlertDialog(title = R.string.str_empty,
            description = R.string.msg_are_you_sure_you_want_to_delete_project,
            isOpened = isOpened,
            onClickPositiveButton = {
                userState.getProjectsLoader.value = true
                userEvent.onDelete(projectName.value) {
                    userState.getProjectsLoader.value = false
                    projectName.value = ""
                }
            },
            onClickNegativeButton = {
                //Do nothing
            })
    }
}

private fun manageProjectImport(
    result: ActivityResult,
    context: Context,
    userEvent: HomeUserEvents,
    navController: NavController

) {
    if (result.resultCode == Activity.RESULT_OK) {
        result.data?.let {
            if (it.data != null) {
                importProject(
                    uri = it.data!!,
                    context = context,
                    userEvent = userEvent,
                    navController = navController
                )
            }
        }
    }
}

private fun manageProjectImportFromDeepLink(
    initialHomeUserState: InitialHomeUserState,
    context: Context,
    userEvent: HomeUserEvents,
    navController: NavController
) {
    if (initialHomeUserState.projectFilePathFromDeepLink.isNotBlank()) {
        try {
            if (!initialHomeUserState.isDeeplinkLoadCompleted) {
                val uri = Uri.parse(initialHomeUserState.projectFilePathFromDeepLink)
                importProject(
                    uri = uri,
                    context = context,
                    userEvent = userEvent,
                    navController = navController
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            context.toast(R.string.msg_unable_to_import_project_file_corrupted)
        } finally {
            initialHomeUserState.isDeeplinkLoadCompleted = true
            initialHomeUserState.projectFilePathFromDeepLink = ""
        }
    }
}

private fun importProject(
    context: Context,
    uri: Uri,
    userEvent: HomeUserEvents,
    navController: NavController
) {
    val dataFromFile = context.contentResolver.openInputStream(uri).use { it!!.reader().readText() }
    userEvent.onImportProject(dataFromFile) { isSuccess, project ->
        if (isSuccess) {
            if (project != null) {
                navController.navigate(route = Screen.CanvasScreen.route.plus("?${Keys.PROJECT_NAME}=${project.name}"))
            }
        } else {
            context.toast(R.string.msg_unable_to_import_project_file_corrupted)
        }
    }
}
