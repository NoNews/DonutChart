package com.nonews.donutchart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nonews.donutchart.chart.DonutChart
import com.nonews.donutchart.chart.stubs.stubs
import com.nonews.donutchart.ui.theme.PieChartTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PieChartTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val slices = remember { mutableStateOf(stubs()) }
                    Box(modifier = Modifier.fillMaxSize()) {
                        DonutChart(
                            slices = slices.value,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(270.dp),
                            strokeSize = 75.dp,
                            onSliceClicked = { clickedSlice ->
                                val current = slices.value
                                val newState = current.map { currentSlice ->
                                    if (clickedSlice.name == currentSlice.name) {
                                        currentSlice.copy(selected = !currentSlice.selected)
                                    } else {
                                        currentSlice
                                    }
                                }
                                slices.value = newState
                            }
                        )
                    }
                }
            }
        }
    }
}
