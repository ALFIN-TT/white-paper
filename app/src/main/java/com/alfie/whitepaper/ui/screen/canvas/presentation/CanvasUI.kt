package com.alfie.whitepaper.ui.screen.canvas.presentation

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alfie.whitepaper.R
import com.alfie.whitepaper.addmob.InterstitialAdHelper
import com.alfie.whitepaper.core.utils.saveAsImage
import com.alfie.whitepaper.core.utils.shareAsPng
import com.alfie.whitepaper.core.utils.shareFile
import com.alfie.whitepaper.core.utils.toast
import com.alfie.whitepaper.data.constants.EXPORT_PROJECT
import com.alfie.whitepaper.data.constants.SAVE_AS_JPEG
import com.alfie.whitepaper.data.constants.SAVE_AS_PNG
import com.alfie.whitepaper.data.constants.SAVE_AS_PROJECT
import com.alfie.whitepaper.data.constants.SHARE
import com.alfie.whitepaper.data.constants.SHARE_PROJECT
import com.alfie.whitepaper.data.constants.SaveOptionType
import com.alfie.whitepaper.ui.common.DrawAlertDialog
import com.alfie.whitepaper.ui.common.canvas.DrawCanvas
import com.alfie.whitepaper.ui.common.canvas.DrawController
import com.alfie.whitepaper.ui.common.canvas.rememberDrawController
import com.alfie.whitepaper.ui.common.colorpicker.ColorSaver
import com.alfie.whitepaper.ui.common.colorpicker.DrawColorPicker
import com.alfie.whitepaper.ui.screen.canvas.state.CanvasEvents
import com.alfie.whitepaper.ui.screen.canvas.state.CanvasUIState
import com.alfie.whitepaper.ui.screen.canvas.viewmodel.CanvasUIViewModel
import kotlinx.coroutines.launch


@Composable
fun CanvasUI(
    navController: NavController = rememberNavController(),
    userState: CanvasUIState = CanvasUIState(),
    viewModel: CanvasUIViewModel
) {
    val context = LocalContext.current
    DrawRootView(navController = navController, userState = userState,
        onHandleEvent = {
            viewModel.handleEventsWithAd(
                event = it,
                onShowAdd = {
                    //viewModel.isAdShowing(isAdShowing = true)
                    InterstitialAdHelper.showInterstitialAd(context) {
                        viewModel.handleEvent(event = it)
                        //viewModel.isAdShowing(isAdShowing = false)
                    }
                },
                onAdSkipped = {
                    viewModel.handleEvent(it)
                }
            )
        }
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun DrawRootView(
    navController: NavController, userState: CanvasUIState, onHandleEvent: (CanvasEvents) -> Unit
) {
    Scaffold {
        Column {
            DrawScreenBody(
                navController = navController,
                userState = userState,
                onHandleEvent = onHandleEvent
            )
        }
    }
}

@Composable
private fun DrawScreenBody(
    navController: NavController,
    userState: CanvasUIState,
    onHandleEvent: (CanvasEvents) -> Unit
) {
    val undoVisibility = rememberSaveable { mutableStateOf(false) }
    val redoVisibility = rememberSaveable { mutableStateOf(false) }
    val colorBarVisibility = rememberSaveable { mutableStateOf(false) }
    val sizeBarVisibility = rememberSaveable { mutableStateOf(false) }
    val currentColor = rememberSaveable(stateSaver = ColorSaver) { mutableStateOf(Color.Red) }
    val bg = Color.White
    val currentBgColor = rememberSaveable(stateSaver = ColorSaver) { mutableStateOf(bg) }
    val currentSize = rememberSaveable { mutableIntStateOf(10) }
    val colorIsBg = rememberSaveable { mutableStateOf(false) }
    val drawController = rememberDrawController()
    val canvasImageSaveOption = rememberSaveable { @SaveOptionType mutableStateOf(0) }
    val idShowSaveDrawingSheet = rememberSaveable { mutableStateOf(false) }
    val isShowNotSavedDrawingDialog = rememberSaveable { mutableStateOf(false) }
    val isOpenedClearCanvasDialog = rememberSaveable { mutableStateOf(false) }


    BackHandler {
        if (undoVisibility.value) isShowNotSavedDrawingDialog.value = true
        else navController.navigateUp()
    }
    Box {
        Column {
            DrawDrawingNotSavedDialog(
                isOpenedSaveDialog = isShowNotSavedDrawingDialog,
                navController = navController
            )
            DrawClearCanvasDialog(
                isOpenedClearCanvasDialog = isOpenedClearCanvasDialog,
                drawController = drawController
            )
            DrawSaveDrawingBottomSheet(
                idShowSaveDrawingSheet = idShowSaveDrawingSheet,
                canvasImageSaveOption = canvasImageSaveOption,
                drawController = drawController,
                uiState = userState,
                onHandleEvent = onHandleEvent,
                navController = navController
            )
            val context = LocalContext.current
            DrawCanvas(drawController = drawController,
                backgroundColor = currentBgColor.value,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f, fill = false),
                bitmapCallback = { imageBitmap, error ->
                    when (canvasImageSaveOption.value) {
                        SAVE_AS_PNG -> {
                            saveAsImage(
                                imageBitmap = imageBitmap,
                                context = context,
                                imageFileType = ".png",
                                imageMimeType = "image/png",
                                bitmapCompressFormat = Bitmap.CompressFormat.PNG
                            )
                            idShowSaveDrawingSheet.value = false
                        }

                        SAVE_AS_JPEG -> {
                            saveAsImage(
                                imageBitmap = imageBitmap,
                                context = context,
                                imageFileType = ".jpeg",
                                imageMimeType = "image/jpeg",
                                bitmapCompressFormat = Bitmap.CompressFormat.JPEG
                            )
                            idShowSaveDrawingSheet.value = false
                        }

                        SHARE -> {
                            shareAsPng(
                                imageBitmap = imageBitmap,
                                context = context,
                            )
                            idShowSaveDrawingSheet.value = false
                        }
                    }
                }) { undoCount, redoCount ->
                sizeBarVisibility.value = false
                colorBarVisibility.value = false
                undoVisibility.value = undoCount != 0
                redoVisibility.value = redoCount != 0
            }
            ControlsBar(
                modifier = Modifier,
                drawController = drawController,
                onSaveClick = {
                    idShowSaveDrawingSheet.value = true
                },
                onColorClick = {
                    colorBarVisibility.value = when (colorBarVisibility.value) {
                        false -> true
                        colorIsBg.value -> true
                        else -> false
                    }
                    colorIsBg.value = false
                    sizeBarVisibility.value = false
                },
                onBgColorClick = {
                    colorBarVisibility.value = when (colorBarVisibility.value) {
                        false -> true
                        !colorIsBg.value -> true
                        else -> false
                    }
                    colorIsBg.value = true
                    sizeBarVisibility.value = false
                },
                onSizeClick = {
                    sizeBarVisibility.value = !sizeBarVisibility.value
                    colorBarVisibility.value = false
                },
                onResetClick = {
                    isOpenedClearCanvasDialog.value = true
                },
                undoVisibility = undoVisibility,
                redoVisibility = redoVisibility,
                colorValue = currentColor,
                bgColorValue = currentBgColor,
                sizeValue = currentSize,
            )
            DrawColorPicker(
                currentColor.value,
                isVisible = colorBarVisibility.value
            ) {
                if (colorIsBg.value) {
                    currentBgColor.value = it
                    drawController.changeBgColor(it)
                } else {
                    currentColor.value = it
                    drawController.changeColor(it)
                }
            }
            BrushSizeSlider(
                isVisible = sizeBarVisibility.value,
                progress = currentSize.intValue,
                progressColor = currentColor.value,
                thumbColor = currentColor.value,
            ) {
                currentSize.intValue = it
                drawController.changeStrokeWidth(it.toFloat())
            }
        }
        if (userState.canvasState.value.drawCanvasPayLoad.path.isNotEmpty()) {
            drawController.importPath(userState.canvasState.value.drawCanvasPayLoad)
            userState.canvasState.value.drawCanvasPayLoad.path = emptyList()
        }
    }
}


@Composable
private fun DrawDrawingNotSavedDialog(
    isOpenedSaveDialog: MutableState<Boolean>,
    navController: NavController
) {
    if (isOpenedSaveDialog.value) {
        DrawAlertDialog(
            title = R.string.msg_unsaved_changes_lost,
            description = R.string.msg_are_you_sure_want_to_exit,
            isOpened = isOpenedSaveDialog,
            onClickPositiveButton = {
                navController.navigateUp()
            },
            onClickNegativeButton = {
                //Do nothing
            }
        )
    }
}


@Composable
private fun DrawClearCanvasDialog(
    isOpenedClearCanvasDialog: MutableState<Boolean>,
    drawController: DrawController
) {
    if (isOpenedClearCanvasDialog.value) {
        DrawAlertDialog(
            title = R.string.msg_your_drawing_will_be_lost,
            description = R.string.msg_do_you_want_to_clear_canvas,
            isOpened = isOpenedClearCanvasDialog,
            onClickPositiveButton = {
                drawController.reset()
            }
        )
    }
}

@Composable
private fun DrawSaveDrawingBottomSheet(
    idShowSaveDrawingSheet: MutableState<Boolean>,
    canvasImageSaveOption: MutableState<Int>,
    drawController: DrawController,
    uiState: CanvasUIState,
    onHandleEvent: (CanvasEvents) -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    if (idShowSaveDrawingSheet.value) {
        DrawSaveDrawingBottomSheet(onSave = { saveOption ->
            when (saveOption.type) {
                SAVE_AS_JPEG, SAVE_AS_PNG, SHARE -> {
                    onHandleEvent(CanvasEvents.SaveAndShare {
                        saveOrShareImageFile(canvasImageSaveOption, drawController, saveOption)
                    })
                }

                SAVE_AS_PROJECT -> {
                    coroutineScope.launch {
                        val thumbnail = drawController.getDrawingAsBase64()
                        onHandleEvent(CanvasEvents.SaveAndShareWithPayLoad(
                            payLoad = drawController.exportPath().apply {
                                this.thumbnail = thumbnail
                            }
                        ) {
                            idShowSaveDrawingSheet.value = false
                            navController.navigateUp()
                        })
                    }
                }

                EXPORT_PROJECT -> {
                    coroutineScope.launch {
                        val thumbnail = drawController.getDrawingAsBase64()
                        onHandleEvent(
                            CanvasEvents.SaveAndShareWithPayLoad(
                                drawController.exportPath().apply {
                                    this.thumbnail = thumbnail
                                }) {
                                context.toast(R.string.str_project_exported_successfully_)
                                idShowSaveDrawingSheet.value = false
                            })
                    }
                }

                SHARE_PROJECT -> {
                    coroutineScope.launch {
                        val thumbnail = drawController.getDrawingAsBase64()
                        shareFile(
                            uiState.drawCanvasPayLoadToString(
                                drawController.exportPath().apply {
                                    this.thumbnail = thumbnail
                                }
                            ), context
                        )
                    }
                }
            }
        }) {
            idShowSaveDrawingSheet.value = false
        }
    }
}