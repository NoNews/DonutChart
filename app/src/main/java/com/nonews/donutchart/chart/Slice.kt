package com.nonews.donutchart.chart

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Immutable
data class Slice(
    val name: String,
    val value: Float, //0 - 1,
    val color: Color,
    val selected: Boolean = false
)