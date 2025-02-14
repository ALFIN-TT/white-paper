package com.alfie.whitepaper.ui.screen.home.usecases


import com.alfie.whitepaper.data.database.room.Project
import kotlinx.coroutines.flow.Flow

interface GetProjectsUseCase {
    suspend operator fun invoke(): Flow<List<Project>>
}