package com.alfie.whitepaper.ui.screen.home.repository

import com.alfie.whitepaper.data.database.room.Project
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun getProjects(): Flow<List<Project>>
    fun getProject(name: String): Flow<Project>
    suspend fun deleteProject(projectName: String)
    suspend fun saveProject(project: Project)
}