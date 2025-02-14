package com.alfie.whitepaper.ui.screen.canvas.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfie.whitepaper.core.ui.core.StateHolder
import com.alfie.whitepaper.core.utils.getCurrentDateTime
import com.alfie.whitepaper.core.utils.saveToDownloads
import com.alfie.whitepaper.data.constants.Keys
import com.alfie.whitepaper.data.database.room.Project
import com.alfie.whitepaper.ui.common.canvas.DrawCanvasPayLoad
import com.alfie.whitepaper.ui.screen.canvas.state.CanvasState
import com.alfie.whitepaper.ui.screen.canvas.usecases.GetProjectUseCase
import com.alfie.whitepaper.ui.screen.canvas.usecases.SaveProjectUseCase
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CanvasUIViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val saveProjectUseCase: SaveProjectUseCase,
    private val getProjectUseCase: GetProjectUseCase
) : ViewModel() {

    private val _canvasState = mutableStateOf(StateHolder(CanvasState()))
    val canvasState: State<StateHolder<CanvasState>> = _canvasState

    private var name: String = savedStateHandle.get<String>(Keys.KEY_PROJECT_NAME) ?: ""

    init {
        if (name.isNotBlank()) getProject(name)
    }

    private fun getProject(name: String) {
        viewModelScope.launch {
            getProjectUseCase.invoke(name).collect {
                if (it.drawing.isNotBlank()) {
                    val json = GsonBuilder().create()
                    val listOfMyClassObject = object : TypeToken<DrawCanvasPayLoad>() {}.type
                    val canvasState = CanvasState(
                        drawCanvasPayLoad =
                        json.fromJson<DrawCanvasPayLoad?>(
                            it.drawing,
                            listOfMyClassObject
                        ).apply { this.name = it.name })
                    _canvasState.value = StateHolder(canvasState)
                }
            }
        }
    }

    private fun saveProject(project: Project) {
        viewModelScope.launch {
            saveProjectUseCase.invoke(project)
        }
    }

    private fun exportProject(
        payLoad: DrawCanvasPayLoad,
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val projectString = makeProjectToJsonString(payLoad)
            saveToDownloads(
                projectString.first.name.plus(".txt"),
                projectString.second.toByteArray()
            )
            withContext(Dispatchers.Main) {
                callback.invoke()
            }
        }

        /*var byteArray: ByteArray?
        viewModelScope.launch(Dispatchers.IO) {
            val jsonData = "{\"name\":\"John\", \"age\":30, \"car\":null}".toByteArray()
            byteArray = encodeFile(generateAesSecretKey(), jsonData)
            saveToDownloads("encoded_file.txt", jsonData!!)
            withContext(Dispatchers.Main) {
                val gson = Gson()
                val jsonStringFromByteArray = String(byteArray!!, StandardCharsets.UTF_8)
                val actualJsonString = gson.fromJson(
                    jsonStringFromByteArray,
                    String::class.java
                )
                Log.e("TAG", "jsonData: $jsonData")
                Log.e("TAG", "encode: ${byteArray.toString()}")
            }
        }*/
    }

    private fun makeProject(drawCanvasPayLoad: DrawCanvasPayLoad): Project {
        val json = GsonBuilder().create()
        val drawing = json.toJson(drawCanvasPayLoad)
        val drawingName = drawCanvasPayLoad.name.ifEmpty { "Drawing_${getCurrentDateTime()}" }
        return Project(
            name = drawingName,
            drawing = drawing,
            thumbnail = drawCanvasPayLoad.thumbnail?:""
        )
    }

    private fun makeProjectToJsonString(drawCanvasPayLoad: DrawCanvasPayLoad): Pair<Project, String> {
        val json = GsonBuilder().create()
        val project = makeProject(drawCanvasPayLoad = drawCanvasPayLoad)
        return Pair(project, json.toJson(project))
    }

    fun drawCanvasPayLoadToString(drawCanvasPayLoad: DrawCanvasPayLoad) =
        makeProjectToJsonString(drawCanvasPayLoad).second


    fun onSaveRequested(
        payLoad: DrawCanvasPayLoad, callback: () -> Unit
    ) {
        val project = makeProject(payLoad)
        saveProject(project)
        callback.invoke()
    }

    fun onExportRequested(
        payLoad: DrawCanvasPayLoad,
        callback: () -> Unit
    ) {
        exportProject(payLoad, callback)
    }
}