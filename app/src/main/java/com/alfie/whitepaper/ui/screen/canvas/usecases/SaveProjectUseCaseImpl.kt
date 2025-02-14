package com.alfie.whitepaper.ui.screen.canvas.usecases

import com.alfie.whitepaper.data.database.room.Project
import com.alfie.whitepaper.ui.screen.home.repository.ProjectRepository

class SaveProjectUseCaseImpl(private val repository: ProjectRepository) : SaveProjectUseCase {
    override suspend fun invoke(project: Project) = repository.saveProject(project)
}