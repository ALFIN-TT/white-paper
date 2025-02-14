package com.alfie.whitepaper.data.constants

import androidx.annotation.IntDef

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.EXPRESSION,
    AnnotationTarget.TYPE
)
@IntDef(DARK_MODE, LIGHT_MODE, BY_SYSTEM)
@Retention(AnnotationRetention.SOURCE)
annotation class DarkThemeOption

const val DARK_MODE: Int = 0
const val LIGHT_MODE: Int = 1
const val BY_SYSTEM: Int = 2
