package com.alfie.whitepaper.ui.screen.canvas.usecases

import com.alfie.whitepaper.ui.screen.home.repository.ProjectRepository

class GetProjectUseCaseImpl(private val repository: ProjectRepository) : GetProjectUseCase {
    override suspend fun invoke(name: String) = repository.getProject(name)
}