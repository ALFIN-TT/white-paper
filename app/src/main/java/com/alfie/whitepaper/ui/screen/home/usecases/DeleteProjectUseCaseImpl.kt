package com.alfie.whitepaper.ui.screen.home.usecases


import com.alfie.whitepaper.data.database.room.Project
import com.alfie.whitepaper.ui.screen.home.repository.ProjectRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ActivityRetainedScoped
class DeleteProjectUseCaseImpl @Inject constructor(
    private val repository: ProjectRepository
) : DeleteProjectUseCase {
    override suspend fun invoke(projectName: String) =
        repository.deleteProject(projectName = projectName)
}