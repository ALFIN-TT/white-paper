package com.alfie.whitepaper.di

import android.content.Context
import com.alfie.whitepaper.data.database.AppPreferenceDataStore
import com.alfie.whitepaper.data.database.room.AppDatabase
import com.alfie.whitepaper.data.repository.AppPreferenceRepository
import com.alfie.whitepaper.data.repository.AppPreferenceRepositoryImpl
import com.alfie.whitepaper.ui.screen.canvas.usecases.GetProjectUseCase
import com.alfie.whitepaper.ui.screen.canvas.usecases.GetProjectUseCaseImpl
import com.alfie.whitepaper.ui.screen.canvas.usecases.SaveProjectUseCase
import com.alfie.whitepaper.ui.screen.canvas.usecases.SaveProjectUseCaseImpl
import com.alfie.whitepaper.ui.screen.home.repository.ProjectRepository
import com.alfie.whitepaper.ui.screen.home.repository.ProjectRepositoryImpl
import com.alfie.whitepaper.ui.screen.home.usecases.DeleteProjectUseCase
import com.alfie.whitepaper.ui.screen.home.usecases.DeleteProjectUseCaseImpl
import com.alfie.whitepaper.ui.screen.home.usecases.GetAppPreferenceUseCase
import com.alfie.whitepaper.ui.screen.home.usecases.GetAppPreferenceUseCaseImpl
import com.alfie.whitepaper.ui.screen.home.usecases.GetProjectsUseCase
import com.alfie.whitepaper.ui.screen.home.usecases.GetProjectsUseCaseImpl
import com.alfie.whitepaper.ui.screen.home.usecases.IsDarkThemeUseCase
import com.alfie.whitepaper.ui.screen.home.usecases.IsDarkThemeUseCaseImpl
import com.alfie.whitepaper.ui.screen.home.usecases.IsDynamicThemeUseCase
import com.alfie.whitepaper.ui.screen.home.usecases.IsDynamicThemeUseCaseImpl
import com.alfie.whitepaper.ui.screen.home.usecases.SetDarkThemeStatusUseCase
import com.alfie.whitepaper.ui.screen.home.usecases.SetDarkThemeStatusUseCaseImpl
import com.alfie.whitepaper.ui.screen.home.usecases.SetDynamicThemeStatusUseCase
import com.alfie.whitepaper.ui.screen.home.usecases.SetDynamicThemeStatusUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.getDatabase(context = context)

    @Provides
    @Singleton
    fun providesProjectRepository(appDatabase: AppDatabase): ProjectRepository =
        ProjectRepositoryImpl(appDatabase)

    @Provides
    @Singleton
    fun providesGetProjectsUseCase(projectRepository: ProjectRepository,@IoDispatcher dispatcher: CoroutineDispatcher): GetProjectsUseCase =
        GetProjectsUseCaseImpl(projectRepository,dispatcher)

    @Provides
    @Singleton
    fun providesGetProjectUseCase(projectRepository: ProjectRepository): GetProjectUseCase =
        GetProjectUseCaseImpl(projectRepository)

    @Provides
    @Singleton
    fun providesSaveProjectUseCase(projectRepository: ProjectRepository): SaveProjectUseCase =
        SaveProjectUseCaseImpl(projectRepository)

    @Provides
    @Singleton
    fun providesDeleteProjectUseCase(projectRepository: ProjectRepository): DeleteProjectUseCase =
        DeleteProjectUseCaseImpl(projectRepository)

    @Provides
    @Singleton
    fun providesAppPreferenceDataStore(@ApplicationContext context: Context): AppPreferenceDataStore =
        AppPreferenceDataStore(context = context)

    @Provides
    @Singleton
    fun providesAppPreferenceRepository(appPreferenceDataStore: AppPreferenceDataStore): AppPreferenceRepository =
        AppPreferenceRepositoryImpl(appPreferenceDataStore)

    @Provides
    @Singleton
    fun providesIsDynamicThemeUseCase(appPreferenceRepository: AppPreferenceRepository): IsDynamicThemeUseCase =
        IsDynamicThemeUseCaseImpl(appPreferenceRepository)

    @Provides
    @Singleton
    fun providesSetDynamicThemeStatusUseCase(appPreferenceRepository: AppPreferenceRepository): SetDynamicThemeStatusUseCase =
        SetDynamicThemeStatusUseCaseImpl(appPreferenceRepository)

    @Provides
    @Singleton
    fun providesIsDarkThemeUseCase(appPreferenceRepository: AppPreferenceRepository): IsDarkThemeUseCase =
        IsDarkThemeUseCaseImpl(appPreferenceRepository)

    @Provides
    @Singleton
    fun providesSetDarkThemeStatusUseCase(appPreferenceRepository: AppPreferenceRepository): SetDarkThemeStatusUseCase =
        SetDarkThemeStatusUseCaseImpl(appPreferenceRepository)

    @Provides
    @Singleton
    fun providesGetAppPreferenceUseCase(appPreferenceRepository: AppPreferenceRepository): GetAppPreferenceUseCase =
        GetAppPreferenceUseCaseImpl(appPreferenceRepository)
}