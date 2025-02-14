package com.alfie.basicnetworkapplication.ui.screens.core

interface Transformer<I, O> {
    fun transform(input: I): O
}