package com.alfie.whitepaper.ui.screen.home.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfie.whitepaper.R
import com.alfie.whitepaper.core.ui.core.StateHolder
import com.alfie.whitepaper.data.constants.BY_SYSTEM
import com.alfie.whitepaper.data.constants.DARK_MODE
import com.alfie.whitepaper.data.constants.DarkThemeOption
import com.alfie.whitepaper.data.constants.LIGHT_MODE
import com.alfie.whitepaper.data.database.room.Project
import com.alfie.whitepaper.ui.common.canvas.DrawCanvasPayLoad
import com.alfie.whitepaper.ui.screen.canvas.usecases.SaveProjectUseCase
import com.alfie.whitepaper.ui.screen.home.state.HomeState
import com.alfie.whitepaper.ui.screen.home.state.InitialHomeUserState
import com.alfie.whitepaper.ui.screen.home.usecases.DeleteProjectUseCase
import com.alfie.whitepaper.ui.screen.home.usecases.GetProjectsUseCase
import com.alfie.whitepaper.ui.screen.home.usecases.SetDarkThemeStatusUseCase
import com.alfie.whitepaper.ui.screen.home.usecases.SetDynamicThemeStatusUseCase
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

    private val _homeState = mutableStateOf(StateHolder(HomeState()))
    val homeState: State<StateHolder<HomeState>> = _homeState

    var projectPathFromDeepLink: InitialHomeUserState = InitialHomeUserState()

    init {
        getProjects()
    }

    private fun getProjects() {
        viewModelScope.launch {
            getProjectsUseCase.invoke().collect {
                val projects = arrayListOf<DrawCanvasPayLoad>()
                it.forEach { project ->
                    projects.add(convertToDrawBoxPayLoad(project))
                }
                val homeState = HomeState(
                    projects = projects
                )
                _homeState.value = StateHolder(homeState)
            }
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
        projectName: String, callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteProjectUseCase.invoke(projectName)
            getProjectsUseCase.invoke().collect {
                val projects = arrayListOf<DrawCanvasPayLoad>()
                it.forEach { project ->
                    projects.add(convertToDrawBoxPayLoad(project))
                }
                val homeState = HomeState(
                    projects = projects
                )
                _homeState.value = StateHolder(homeState)
            }
            callback()
        }
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