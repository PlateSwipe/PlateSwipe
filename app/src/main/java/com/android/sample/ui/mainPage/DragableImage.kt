package com.android.sample.ui.mainPage

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

enum class DragStates {
  left,
  Center,
  right,
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)
@Composable
fun DragableRecipe(
    state: AnchoredDraggableState<DragStates>,
    content: @Composable BoxScope.() -> Unit,
    startAction: @Composable (BoxScope.() -> Unit)? = {},
    endAction: @Composable (BoxScope.() -> Unit)? = {}
) {

  Box(modifier = Modifier) {
    endAction?.let { endAction() }

    startAction?.let { startAction() }
    Box(
        modifier =
            Modifier.fillMaxWidth()
                .align(Alignment.CenterStart)
                .offset {
                  IntOffset(
                      x = -state.requireOffset().roundToInt(),
                      y = 0,
                  )
                }
                .anchoredDraggable(state, Orientation.Horizontal, reverseDirection = true),
        content = content)
  }
}
