package com.alfie.whitepaper.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.alfie.whitepaper.data.constants.SaveOptionType

data class SaveOption(
    @SaveOptionType
    val type: Int,
    @DrawableRes
    val iconRes: Int,
    @StringRes
    val name: Int,
    @StringRes
    val description: Int
)