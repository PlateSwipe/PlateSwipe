package com.android.sample.animation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.resources.C.Dimension.LoadingCook.COOK_SIZE
import com.android.sample.resources.C.Dimension.LoadingCook.ROTATION_DURATION
import com.android.sample.resources.C.Dimension.LoadingCook.ROTATION_MAX
import com.android.sample.resources.C.Dimension.LoadingCook.ROTATION_MIN

@Composable
fun LoadingCook() {
  // Infinite rotation using rememberInfiniteTransition
  val infiniteTransition = rememberInfiniteTransition(label = "Rotation transition")
  val rotation by
      infiniteTransition.animateFloat(
          initialValue = ROTATION_MIN,
          targetValue = ROTATION_MAX,
          animationSpec =
              infiniteRepeatable(
                  animation =
                      tween(
                          durationMillis = ROTATION_DURATION,
                          easing = LinearEasing), // 2 seconds per rotation
                  repeatMode = RepeatMode.Restart),
          label = "Rotation of the image")

  // Load image painter resource (replace with your actual drawable resource)
  val painter = painterResource(id = R.drawable.cook_loading)

  // Apply rotation to the image
  Image(
      painter = painter,
      contentDescription = "Rotating Image",
      modifier = Modifier.size(COOK_SIZE.dp).rotate(rotation))
}
