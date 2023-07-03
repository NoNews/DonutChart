@file:OptIn(ExperimentalTextApi::class)

package com.nonews.donutchart.chart

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch


@Composable
fun DonutChart(
    modifier: Modifier = Modifier,
    slices: List<Slice>,
    strokeSize: Dp,
    shadowSize: Dp = 8.dp,
    shadowRadiusThreshold: Float = 1.34f,
    textStyle: TextStyle = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold

    ),
    onSliceClicked: (Slice) -> Unit,
) {
    val anglesInfo = remember(slices) { mutableStateListOf<SliceAnglesInfo>() }
    val scales = remember { slices.map { Animatable(1f) } }
    val textMeasurer = rememberTextMeasurer()

    val textMeasureResults = remember(slices) {
        slices.map {

            textMeasurer.measure(
                text = AnnotatedString(it.value.toString()),
                style = textStyle
            )
        }
    }

    LaunchedEffect(slices, scales) {
        launch {
            slices.forEachIndexed { index, slice ->
                val currentScale = scales[index]
                if (slice.selected) {
                    currentScale.animateTo(1.05f)
                } else {
                    currentScale.animateTo(1.0f)
                }
            }
        }
    }

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        if (!isClickAreaBelongsCircle(
                                clickOffset = offset,
                                canvasSize = size,
                                strokeSize = strokeSize.toPx()
                            )
                        ) {
                            return@detectTapGestures
                        }
                        val clickedArea = detectClickArea(clickOffset = offset, canvasSize = size)
                        val sliceIndex =
                            anglesInfo.indexOfFirst { sliceInfo -> clickedArea in sliceInfo.startAngle..sliceInfo.endAngle }
                        onSliceClicked.invoke(slices[sliceIndex])
                    }
                )
            }
    ) {
        var startAngle = 0F
        var totalPercentage = 0F
        slices.forEach {
            totalPercentage += it.value
        }
        val radius = size.width / 2f

        slices.forEachIndexed { index, data ->
            val slicePercentage = data.value / totalPercentage
            val sweepAngle = slicePercentage * 360f

            val sliceCenterOffset = calculateSliceCenterOffset(
                chartRadius = radius,
                startAngle = startAngle,
                sweepAngle = sweepAngle
            )

            scale(scale = scales[index].value) {
                drawArc(
                    color = data.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeSize.toPx())
                )

                val textMeasureResult = textMeasureResults[index]
                val textPositionOffset = calculateTextPositionOffset(
                    center = center,
                    sliceCenter = sliceCenterOffset,
                    textWidth = textMeasureResult.size.width.toFloat(),
                    textHeight = textMeasureResult.size.height.toFloat()
                )

                drawText(
                    textLayoutResult = textMeasureResult,
                    color = Color.White,
                    topLeft = textPositionOffset
                )

                val shadowRadius = radius / shadowRadiusThreshold
                drawArc(
                    color = Color.Black.copy(alpha = 0.2f),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(shadowSize.toPx()),
                    topLeft = Offset(
                        x = center.x - shadowRadius,
                        y = center.y - shadowRadius
                    ),
                    size = Size(
                        width = shadowRadius * 2f,
                        height = shadowRadius * 2f
                    )
                )
            }

            anglesInfo.add(
                SliceAnglesInfo(
                    startAngle = startAngle,
                    endAngle = startAngle + sweepAngle,
                )
            )
            startAngle += sweepAngle
        }
    }


}
