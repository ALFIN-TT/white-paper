package com.alfie.whitepaper.ui.screen.home.usecases


import com.alfie.whitepaper.data.database.room.Project
import com.alfie.whitepaper.di.IoDispatcher
import com.alfie.whitepaper.ui.screen.home.repository.ProjectRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetProjectsUseCaseImpl @Inject constructor(
    private val repository: ProjectRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GetProjectsUseCase {
    override fun invoke(): Flow<List<Project>> = getProjectsFlow()


    private fun getProjectsFlow(): Flow<List<Project>> {
        return repository.getProjects()
            .flowOn(ioDispatcher)
    }
}