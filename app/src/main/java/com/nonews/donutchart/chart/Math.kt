package com.nonews.donutchart.chart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


internal fun Float.toRadians(): Float {
    return (this * Math.PI / 180f).toFloat()
}

internal fun calculateSliceCenterOffset(
    chartRadius: Float,
    startAngle: Float,
    sweepAngle: Float
): Offset {
    val sliceCenterRadians = (startAngle + sweepAngle / 2).toRadians()

    return Offset(
        x = (chartRadius * cos(sliceCenterRadians)),
        y = (chartRadius * sin(sliceCenterRadians))
    )
}

internal fun calculateTextPositionOffset(
    center: Offset,
    textWidth: Float,
    textHeight: Float,
    sliceCenter: Offset
): Offset {
    val textXOffset = textWidth / 2f
    val textYOffset = textHeight / 2f
    val textX = center.x + sliceCenter.x - textXOffset
    val textY = center.y + sliceCenter.y - textYOffset

    return Offset(x = textX, y = textY)
}


internal fun isClickAreaBelongsCircle(
    clickOffset: Offset,
    canvasSize: IntSize,
    strokeSize: Float
): Boolean {
    val x = clickOffset.x
    val y = clickOffset.y

    val centerX = canvasSize.width / 2
    val centerY = canvasSize.height / 2
    val radius = kotlin.math.min(canvasSize.width, canvasSize.height) / 2

    val translatedX = x - centerX
    val translatedY = y - centerY

    val distance = sqrt(translatedX.pow(2) + translatedY.pow(2))

    if (strokeSize != -1f) {
        val strokeWidth = strokeSize / 2
        return distance >= (radius - strokeWidth) && distance <= (radius + strokeWidth)
    }
    return distance <= radius
}

internal fun detectClickArea(clickOffset: Offset, canvasSize: IntSize): Float {
    val centerX = canvasSize.width / 2
    val centerY = canvasSize.height / 2
    return (-atan2(
        x = centerY - clickOffset.y,
        y = centerX - clickOffset.x
    ) * (180f / PI).toFloat() - 90f).mod(360f)
}
