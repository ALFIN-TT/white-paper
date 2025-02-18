package com.alfie.whitepaper.ui

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alfie.whitepaper.R
import com.alfie.whitepaper.addmob.InterstitialAdHelper.removeInterstitial
import com.alfie.whitepaper.core.utils.toast
import com.alfie.whitepaper.data.constants.BY_SYSTEM
import com.alfie.whitepaper.ui.navigation.ScreenNavigation
import com.alfie.whitepaper.ui.theme.WhitePaperTheme
import com.google.android.gms.ads.MobileAds
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.common.IntentSenderForResultStarter
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val APP_UPDATE_REQUEST_CODE = 101
    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var viewModel: MainActivityViewModel
    private var updateType = AppUpdateType.IMMEDIATE
    private lateinit var _navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        checkForAppUpdate()
        setContent {
            WhitePaperTheme(
                dynamicColor = viewModel.dynamicThemeState.value,
                darkTheme = viewModel.darkThemeState.intValue
            ) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    _navController = navController
                    ScreenNavigation(
                        isEnabledDynamicTheme = viewModel.dynamicThemeState,
                        isEnabledDarkTheme = viewModel.darkThemeState,
                        navController = navController
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (::_navController.isInitialized) _navController.handleDeepLink(intent)
    }

    override fun onResume() {
        super.onResume()
        resumeAppUpdate()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeInterstitial()
        unregisterAppUpdate()
    }


    /***
     * Initialize all variables here
     */
    private fun initialize() {
        MobileAds.initialize(this)
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
    }

    /***
     * Listener for starting and waiting app update.
     */
    private val appUpdateLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.data == null) return@registerForActivityResult
        if (result.resultCode == APP_UPDATE_REQUEST_CODE) {
            toast(R.string.str_downloading_started)
            if (result.resultCode != Activity.RESULT_OK) {
                toast(R.string.str_update_failed)
            }
        }
    }

    /***
     * Listener for start app update.
     */
    private val updateResultStarter =
        IntentSenderForResultStarter { intent, _, fillInIntent, flagsMask, flagsValues, _, _ ->
            val request = IntentSenderRequest.Builder(intent)
                .setFillInIntent(fillInIntent)
                .setFlags(flagsValues, flagsMask)
                .build()
            appUpdateLauncher.launch(request)
        }

    /***
     * Listener for install app after download.
     */
    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            toast(R.string.msg_download_successful_restarting_app_in_5_sec, Toast.LENGTH_LONG)
            lifecycleScope.launch {
                delay(5.seconds)
                appUpdateManager.completeUpdate()
            }
        }
    }

    /***
     * App update check
     */
    private fun checkForAppUpdate() {
        if (updateType == AppUpdateType.FLEXIBLE) appUpdateManager.registerListener(
            installStateUpdatedListener
        )

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            val isUpdateAvailable =
                appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = when (updateType) {
                AppUpdateType.FLEXIBLE -> appUpdateInfo.isFlexibleUpdateAllowed
                AppUpdateType.IMMEDIATE -> appUpdateInfo.isImmediateUpdateAllowed
                else -> false
            }
            if (isUpdateAvailable && isUpdateAllowed) {
                startAppUpdateFlowForResult(appUpdateInfo = appUpdateInfo)
            }
        }
    }

    /***
     * Starting and waiting for App update.
     */
    private fun startAppUpdateFlowForResult(appUpdateInfo: AppUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                updateResultStarter,
                AppUpdateOptions.newBuilder(updateType).build(),
                APP_UPDATE_REQUEST_CODE
            )
        } catch (exception: IntentSender.SendIntentException) {
            toast(exception.message.toString())
        }
    }

    /***
     * Resuming app updates
     */
    private fun resumeAppUpdate() {
        if (updateType == AppUpdateType.IMMEDIATE) {
            appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    startAppUpdateFlowForResult(appUpdateInfo = appUpdateInfo)
                }
            }
        }
    }

    /***
     * Unregister app update callbacks.
     */
    private fun unregisterAppUpdate() {
        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.unregisterListener(installStateUpdatedListener)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AppPreview() {
    WhitePaperTheme {
        val navController = rememberNavController()
        val isEnabledDynamicTheme = rememberSaveable {
            mutableStateOf(true)
        }
        val isEnabledDarkTheme = rememberSaveable {
            mutableIntStateOf(BY_SYSTEM)
        }
        ScreenNavigation(
            isEnabledDynamicTheme = isEnabledDynamicTheme,
            isEnabledDarkTheme = isEnabledDarkTheme,
            navController = navController
        )
    }
}