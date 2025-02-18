package com.alfie.whitepaper.ui.screen.home.viewmodel

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfie.whitepaper.R
import com.alfie.whitepaper.data.constants.BY_SYSTEM
import com.alfie.whitepaper.data.constants.DARK_MODE
import com.alfie.whitepaper.data.constants.DarkThemeOption
import com.alfie.whitepaper.data.constants.LIGHT_MODE
import com.alfie.whitepaper.data.database.room.Project
import com.alfie.whitepaper.ui.common.canvas.DrawCanvasPayLoad
import com.alfie.whitepaper.ui.screen.canvas.usecases.SaveProjectUseCase
import com.alfie.whitepaper.ui.screen.home.state.HomeEvent
import com.alfie.whitepaper.ui.screen.home.state.HomeUIState
import com.alfie.whitepaper.ui.screen.home.state.InitialHomeUserState
import com.alfie.whitepaper.ui.screen.home.usecases.DeleteProjectUseCase
import com.alfie.whitepaper.ui.screen.home.usecases.GetProjectsUseCase
import com.alfie.whitepaper.ui.screen.home.usecases.SetDarkThemeStatusUseCase
import com.alfie.whitepaper.ui.screen.home.usecases.SetDynamicThemeStatusUseCase
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class HomeUIViewModel @Inject constructor(
    private val getProjectsUseCase: GetProjectsUseCase,
    private val deleteProjectUseCase: DeleteProjectUseCase,
    private val saveProjectUseCase: SaveProjectUseCase,
    private val setDynamicThemeStatusUseCase: SetDynamicThemeStatusUseCase,
    private val setDarkThemeStatusUseCase: SetDarkThemeStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState = _uiState.asStateFlow()

    init {
        getProjects()
    }

    fun init(
        isEnableDynamicTheme: MutableState<Boolean>,
        isEnabledDarkTheme: MutableState<Int>,
        projectFromDeepLink: String
    ) {
        _uiState.update {
            it.copy(
                isEnableDynamicTheme = isEnableDynamicTheme,
                isEnableDarkTheme = isEnabledDarkTheme,
                initialState = InitialHomeUserState(projectFilePathFromDeepLink = projectFromDeepLink)
            )
        }
    }

    private fun getProjects() {
        _uiState.update { it.copy(isLoading = true) }
        getProjectsUseCase().onEach {
            val projects = arrayListOf<DrawCanvasPayLoad>()
            it.forEach { project ->
                projects.add(convertToDrawBoxPayLoad(project))
            }
            _uiState.update { state ->
                state.copy(
                    projects = projects.toImmutableList(),
                    isLoading = false
                )
            }
        }.launchIn(viewModelScope)
    }

    fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.DeleteProject -> onDeleteProjectRequested(event.id)
            is HomeEvent.DarkThemeOptionName -> getDarkThemeOptionName(event.optionType)
            is HomeEvent.SetDynamicTheme -> onDynamicThemeStatusChangeRequested(event.isEnable)
            is HomeEvent.SetDarkTheme -> onDarkThemeStatusChangeRequested(event.option)
        }
    }

    private fun convertToDrawBoxPayLoad(project: Project): DrawCanvasPayLoad {
        val json = GsonBuilder().create()
        return if (project.drawing.isNotBlank()) {
            val listOfMyClassObject = object : TypeToken<DrawCanvasPayLoad>() {}.type
            json.fromJson<DrawCanvasPayLoad?>(project.drawing, listOfMyClassObject)
                .apply { name = project.name }
        } else DrawCanvasPayLoad()
    }


    fun getDarkThemeOptionName(@DarkThemeOption optionType: Int): Int = when (optionType) {
        LIGHT_MODE -> R.string.str_light
        DARK_MODE -> R.string.str_dark
        BY_SYSTEM -> R.string.str_by_system
        else -> R.string.str_empty
    }

    fun onDeleteProjectRequested(
        projectName: String
    ) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            deleteProjectUseCase(projectName)
        }
        _uiState.update { it.copy(isLoading = true) }
    }

    fun onDarkThemeStatusChangeRequested(
        @DarkThemeOption darkThemeOption: Int
    ) {
        viewModelScope.launch {
            setDarkThemeStatusUseCase.invoke(darkThemeOption)
        }
    }

    fun onDynamicThemeStatusChangeRequested(
        isDynamicTheme: Boolean
    ) {
        viewModelScope.launch {
            setDynamicThemeStatusUseCase.invoke(isDynamicTheme)
        }
    }

    fun onImportProject(
        importedData: String, callback: (Boolean, Project?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            var project: Project? = null
            val json = GsonBuilder().create()
            val isSuccess = try {
                if (importedData.isNotBlank()) {
                    val projectType = object : TypeToken<Project>() {}.type
                    project =
                        json.fromJson<Project?>(importedData, projectType).apply { isTemp = true }
                    saveProjectUseCase.invoke(project)
                    true
                } else false
            } catch (e: Exception) {
                false
            }
            withContext(Dispatchers.Main) {
                callback.invoke(isSuccess, project)
            }
        }

        /*Log.e("TAG", "decode: $uri")
          Log.e("TAG", "jsonData: ${String(uri, StandardCharsets.UTF_8)}")

          decodeFile(generateAesSecretKey(), byteArray)?.let {
              Log.e("TAG", "picked byte array: $byteArray")
              Log.e("TAG", "decode: $it")
              Log.e("TAG", "jsonData: ${String(it, StandardCharsets.UTF_8)}")
          }
          val gson = Gson()
          val jsonStringFromByteArray = String(jsonByteArray!!, StandardCharsets.UTF_8)
          val actualJsonString = gson.fromJson(
              jsonStringFromByteArray,
              String::class.java
          )
          Log.e("TAG", "decode: ${jsonByteArray.toString()}")
          Log.e("TAG", "jsonData: ${String(jsonByteArray!!, StandardCharsets.UTF_8)}")*/
    }
}