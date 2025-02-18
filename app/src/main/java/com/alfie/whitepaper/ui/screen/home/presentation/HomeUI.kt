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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alfie.whitepaper.R
import com.alfie.whitepaper.core.utils.shareImageFromAssets
import com.alfie.whitepaper.core.utils.toast
import com.alfie.whitepaper.data.constants.BY_SYSTEM
import com.alfie.whitepaper.data.constants.DarkThemeOption
import com.alfie.whitepaper.data.constants.Keys
import com.alfie.whitepaper.data.database.room.Project
import com.alfie.whitepaper.ui.common.DrawAlertDialog
import com.alfie.whitepaper.ui.common.ProgressLoader
import com.alfie.whitepaper.ui.common.addmob.AdmobBanner
import com.alfie.whitepaper.ui.navigation.Screen
import com.alfie.whitepaper.ui.screen.home.state.HomeEvent
import com.alfie.whitepaper.ui.screen.home.state.HomeUIState
import com.alfie.whitepaper.ui.screen.home.state.InitialHomeUserState
import com.alfie.whitepaper.ui.screen.home.viewmodel.HomeUIViewModel
import com.alfie.whitepaper.ui.theme.WhitePaperTheme


@Composable
fun HomeUI(
    viewModel: HomeUIViewModel = hiltViewModel<HomeUIViewModel>(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    DrawRootView(
        navController = navController,
        uiState = uiState,
        onHandleEvent = { viewModel.handleEvent(it) },
        onFindDarkThemeName = { viewModel.getDarkThemeOptionName(it) },
        onImportProject = {
            val dataFromFile =
                context.contentResolver.openInputStream(it).use { it!!.reader().readText() }
            viewModel.onImportProject(dataFromFile) { isSuccess, project ->
                importProject(context, navController, isSuccess, project)
            }
        }
    )
}

@Composable
private fun DrawRootView(
    navController: NavController,
    uiState: HomeUIState,
    onHandleEvent: (HomeEvent) -> Unit,
    onFindDarkThemeName: (@DarkThemeOption Int) -> Int,
    onImportProject: (Uri) -> Unit
) {
    val isOpenedAppPreferenceDialog = rememberSaveable { mutableStateOf(false) }
    val isOpenedDeleteProjectDialog = rememberSaveable { mutableStateOf(false) }
    val selectedProjectName = rememberSaveable { mutableStateOf("") }

    Scaffold(
        content = {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            ) {
                DrawPreferenceDialog(
                    uiState = uiState,
                    onHandleEvent = onHandleEvent,
                    isOpened = isOpenedAppPreferenceDialog,
                    onFindDarkThemeName = onFindDarkThemeName
                )
                DrawDeleteProjectDialog(
                    projectName = selectedProjectName,
                    onHandleEvent = onHandleEvent,
                    isOpened = isOpenedDeleteProjectDialog
                )
                DrawScreenBody(
                    isOpenedAppPreferenceDialog = isOpenedAppPreferenceDialog,
                    isOpenedDeleteProjectDialog = isOpenedDeleteProjectDialog,
                    selectedProjectName = selectedProjectName,
                    uiState = uiState,
                    onImportProject = onImportProject,
                    navController = navController
                )
            }
        },
        bottomBar = {
            AdmobBanner(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(top = 8.dp, bottom = 4.dp)
            )
        })
}


@Composable
private fun DrawScreenBody(
    isOpenedAppPreferenceDialog: MutableState<Boolean>,
    isOpenedDeleteProjectDialog: MutableState<Boolean>,
    selectedProjectName: MutableState<String>,
    uiState: HomeUIState,
    onImportProject: (Uri) -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    LaunchedEffect(uiState.initialState) {
        manageProjectImportFromDeepLink(
            initialHomeUserState = uiState.initialState,
            context = context,
            onImportProject = onImportProject,
        )
    }
    val startForResult = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        manageProjectImport(
            result = result,
            onImportProject = onImportProject
        )
    }
    ProgressLoader(showProgressBar = uiState.isLoading)
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(
            start = 12.dp, top = 16.dp, end = 12.dp
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
                count = uiState.projects.size
            ) {
                DrawProjectCard(
                    count = it,
                    isOpenedDeleteProjectDialog = isOpenedDeleteProjectDialog,
                    selectedProjectName = selectedProjectName,
                    uiState = uiState,
                    navController = navController
                )
            }
            item(span = { GridItemSpan(2) }) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                )
            }
        })
}


@Composable
private fun DrawProjectCard(
    count: Int,
    isOpenedDeleteProjectDialog: MutableState<Boolean>,
    selectedProjectName: MutableState<String>,
    uiState: HomeUIState,
    navController: NavController
) {
    val drawBoxPayLoad = uiState.projects[count]
    ProjectCard(drawBoxPayLoad, onclick = {
        navController.navigate(route = Screen.CanvasScreen.route.plus("?${Keys.PROJECT_NAME}=${drawBoxPayLoad.name}"))
    }, onLongPress = {
        selectedProjectName.value = drawBoxPayLoad.name
        isOpenedDeleteProjectDialog.value = true
    })
}

@Composable
private fun DrawPreferenceDialog(
    isOpened: MutableState<Boolean>,
    uiState: HomeUIState,
    onHandleEvent: (HomeEvent) -> Unit,
    onFindDarkThemeName: (@DarkThemeOption Int) -> Int,
) {
    if (isOpened.value) {
        DrawAppPreferenceDialog(
            isOpened = isOpened,
            uiState = uiState,
            onHandleEvent = onHandleEvent,
            onFindDarkThemeName = onFindDarkThemeName
        )
    }
}

@Composable
private fun DrawDeleteProjectDialog(
    projectName: MutableState<String>,
    isOpened: MutableState<Boolean>,
    onHandleEvent: (HomeEvent) -> Unit
) {
    if (isOpened.value) {
        DrawAlertDialog(title = R.string.str_empty,
            description = R.string.msg_are_you_sure_you_want_to_delete_project,
            isOpened = isOpened,
            onClickPositiveButton = {
                onHandleEvent(HomeEvent.DeleteProject(projectName.value))
            })
    }
}

private fun manageProjectImport(
    result: ActivityResult,
    onImportProject: (Uri) -> Unit,
) {
    if (result.resultCode == Activity.RESULT_OK) {
        result.data?.let {
            if (it.data != null) {
                onImportProject(it.data!!)
            }
        }
    }
}

private fun manageProjectImportFromDeepLink(
    initialHomeUserState: InitialHomeUserState,
    context: Context,
    onImportProject: (Uri) -> Unit,
) {
    if (initialHomeUserState.projectFilePathFromDeepLink.isNotBlank()) {
        try {
            if (!initialHomeUserState.isDeeplinkLoadCompleted) {
                val uri = Uri.parse(initialHomeUserState.projectFilePathFromDeepLink)
                onImportProject(uri)
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
    navController: NavController,
    isSuccess: Boolean,
    project: Project?,
) {
    if (isSuccess) {
        if (project != null) {
            navController.navigate(route = Screen.CanvasScreen.route.plus("?${Keys.PROJECT_NAME}=${project.name}"))
        }
    } else {
        context.toast(R.string.msg_unable_to_import_project_file_corrupted)
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    WhitePaperTheme {
        val navController = rememberNavController()
        DrawRootView(
            navController = navController,
            uiState = HomeUIState(),
            onHandleEvent = {},
            onFindDarkThemeName = { BY_SYSTEM },
            onImportProject = {}
        )
    }
}
