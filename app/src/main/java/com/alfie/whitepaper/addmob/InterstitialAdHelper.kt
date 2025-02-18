package com.alfie.whitepaper.addmob

import android.content.Context
import com.alfie.whitepaper.R
import com.alfie.whitepaper.core.utils.getActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


object InterstitialAdHelper {

    var mInterstitialAd: InterstitialAd? = null

    fun showInterstitialAd(context: Context, onAdCompleted: () -> Unit) {

        val activity = context.getActivity()

        InterstitialAd.load(
            context,
            context.getString(R.string.ad_mob_unit_id_interstitial), //Change this with your own AdUnitID!
            AdRequest.Builder().build(), object : InterstitialAdLoadCallback() {

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                    onAdCompleted()
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    mInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {

                            override fun onAdFailedToShowFullScreenContent(e: AdError) {
                                mInterstitialAd = null
                                onAdCompleted()
                            }

                            override fun onAdDismissedFullScreenContent() {
                                mInterstitialAd = null
                                onAdCompleted()
                            }
                        }
                    activity?.let { mInterstitialAd?.show(activity) }
                }
            }
        )
    }

    fun removeInterstitial() {
        mInterstitialAd?.fullScreenContentCallback = null
        mInterstitialAd = null
    }

}
