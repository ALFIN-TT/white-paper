package com.alfie.whitepaper.ui.screen.home.usecases


import com.alfie.whitepaper.data.model.AppPreference
import com.alfie.whitepaper.data.repository.AppPreferenceRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ActivityRetainedScoped
class GetAppPreferenceUseCaseImpl @Inject constructor(
    private val repository: AppPreferenceRepository
) : GetAppPreferenceUseCase {
    override suspend fun invoke(): Flow<AppPreference> = repository.getAppPreferences()
}