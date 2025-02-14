package com.alfie.whitepaper.ui.screen.home.repository


import com.alfie.whitepaper.data.database.room.Project
import com.alfie.whitepaper.data.database.room.AppDatabase
import com.alfie.whitepaper.data.database.room.ProjectDao
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ActivityRetainedScoped
class ProjectRepositoryImpl @Inject constructor(
    appDatabase: AppDatabase
) : ProjectRepository {

    private var projectDao: ProjectDao = appDatabase.projectDao()

    override fun getProjects(): Flow<List<Project>> = projectDao.getAllProjects()

    override fun getProject(name: String): Flow<Project> = projectDao.getProject(name = name)

    override suspend fun deleteProject(projectName: String) = projectDao.deleteProject(projectName)

    override suspend fun saveProject(project: Project) = projectDao.saveProject(project)
}
