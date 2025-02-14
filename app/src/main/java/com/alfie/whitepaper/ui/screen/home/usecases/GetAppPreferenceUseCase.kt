package com.alfie.whitepaper.ui.screen.home.usecases

import com.alfie.whitepaper.data.model.AppPreference
import kotlinx.coroutines.flow.Flow

interface GetAppPreferenceUseCase {
    suspend operator fun invoke(): Flow<AppPreference>

}