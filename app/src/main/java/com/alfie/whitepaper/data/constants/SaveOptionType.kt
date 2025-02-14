package com.alfie.whitepaper.data.constants

import androidx.annotation.IntDef

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.EXPRESSION)
@IntDef(SAVE_AS_PNG, SAVE_AS_JPEG, SAVE_AS_PROJECT, SHARE, EXPORT_PROJECT, SHARE_PROJECT)
@Retention(AnnotationRetention.SOURCE)
annotation class SaveOptionType

const val SAVE_AS_PNG: Int = 1
const val SAVE_AS_JPEG: Int = 2
const val SAVE_AS_PROJECT: Int = 3
const val SHARE: Int = 4
const val EXPORT_PROJECT: Int = 5
const val SHARE_PROJECT: Int = 6

