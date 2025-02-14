package com.alfie.whitepaper.ui.screen.canvas.usecases

import com.alfie.whitepaper.data.database.room.Project

interface SaveProjectUseCase {
    suspend operator fun invoke(project: Project)
}