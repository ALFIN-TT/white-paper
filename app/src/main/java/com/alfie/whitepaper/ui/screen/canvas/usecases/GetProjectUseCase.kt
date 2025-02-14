package com.alfie.whitepaper.ui.screen.canvas.usecases

import com.alfie.whitepaper.data.database.room.Project
import kotlinx.coroutines.flow.Flow

interface GetProjectUseCase {
    suspend operator fun invoke(name: String): Flow<Project>
}