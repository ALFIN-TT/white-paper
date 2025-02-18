package com.alfie.whitepaper.ui.screen.canvas.usecases

import com.alfie.whitepaper.data.repository.AppPreferenceRepository
import com.alfie.whitepaper.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HandleAdsUseCase @Inject constructor(
    private val appPreferenceRepository: AppPreferenceRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    operator fun invoke(
        onShowAdd: () -> Unit,
        onAdSkipped: () -> Unit
    ) {
        handleAds(onShowAdd, onAdSkipped)
    }

    private fun handleAds(
        onShowAdd: () -> Unit,
        onAdSkipped: () -> Unit
    ) {
        CoroutineScope(ioDispatcher).launch {
            appPreferenceRepository.getAppPreferences().first().let { userPreferences ->
                val adFreeCount = userPreferences.adFreeCount
                if (adFreeCount > FREE_ADS_SKIPP_COUNT) {
                    withContext(Dispatchers.Main) { onShowAdd() }
                    appPreferenceRepository.updateAdFreeCount(0)
                } else {
                    appPreferenceRepository.updateAdFreeCount(adFreeCount + 1)
                    withContext(Dispatchers.Main) { onAdSkipped() }
                }
            }
        }
    }

    companion object {
        const val FREE_ADS_SKIPP_COUNT = 1
    }
}
