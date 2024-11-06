package com.android.sample.ui.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.sample.resources.C.Tag.DELAY_RESPAWN
import com.android.sample.resources.C.Tag.DELAY_SPAWN
import com.android.sample.resources.C.Tag.FRUIT_SIZE
import com.android.sample.resources.C.Tag.GRAVITY
import com.android.sample.resources.C.Tag.INITIAL_SPEED
import com.android.sample.resources.C.Tag.MAX_ROTATION_START
import com.android.sample.resources.C.Tag.NUMBER_FRUIT_MAX
import com.android.sample.resources.C.Tag.ROTATION_DELTA
import com.android.sample.resources.C.Tag.TIME_DELAY
import com.android.sample.resources.C.Tag.TIME_DELTA
import com.android.sample.resources.C.Tag.TIME_INIT
import com.android.sample.resources.C.Tag.UNIT
import com.android.sample.resources.C.Tag.fruitImages
import kotlin.random.Random
import kotlinx.coroutines.delay

// List of image resources for fruits in drawable

/**
 * Data class to represent a falling fruit
 *
 * @param imageRes: Image resource for the fruit
 * @param initialPositionX: Initial X position of the fruit
 * @param initialPositionY: Initial Y position of the fruit
 * @param initialVelocity: Initial velocity of the fruit
 */
class FallingFruit(
    var imageRes: Int,
    var initialPositionX: Float,
    var initialPositionY: Float,
    var initialVelocity: Float
) {
  var positionY by mutableFloatStateOf(initialPositionY)
  var positionX by mutableFloatStateOf(initialPositionX)
  private var velocity by mutableFloatStateOf(initialVelocity)
  private val rotationDelta =
      (Random.nextFloat() * MAX_ROTATION_START) - MAX_ROTATION_START / 2 // Random rotation change
  var rotation by mutableFloatStateOf(rotationDelta) // Random initial rotation

  /**
   * Set a random fruit image and position for the fruit
   *
   * @param screenWidth: Width of the screen
   * @param fruitSize: Size of the fruit
   */
  fun setRandomFruit(
      screenWidth: Float,
      fruitSize: Float,
  ) {
    val xPos = getXPos(screenWidth, fruitSize)
    imageRes = fruitImages.random()
    positionX = xPos
    positionY = -fruitSize
    velocity = rotationDelta
  }

  /**
   * Simulate the fall of the fruit
   *
   * @param screenHeight: Height of the screen
   * @param onFinished: Callback to trigger when the fall is complete
   */
  suspend fun fall(screenHeight: Float, onFinished: () -> Unit) {
    var time = TIME_INIT
    while (positionY < screenHeight) {
      // Delay to simulate the time passing (simulate FPS)
      delay(TIME_DELAY)
      time += TIME_DELTA
      velocity += GRAVITY * time
      positionY += velocity * TIME_DELTA
      rotation -= rotationDelta * ROTATION_DELTA // Small random rotation change
    }

    // Delay to show the fruit at the bottom
    delay(DELAY_RESPAWN)
    onFinished()
  }
}

/**
 * Composable function to display a loading animation with falling fruits
 *
 * @param onFinish: Callback to trigger when the animation is complete
 * @param duration: Duration of the animation
 */
@Composable
fun LoadingAnimation(onFinish: () -> Unit, duration: Long = Long.MIN_VALUE) {
  val configuration = LocalConfiguration.current
  val screenWidth = configuration.screenWidthDp.dp
  val screenHeight = configuration.screenHeightDp.dp
  val fruitSize = FRUIT_SIZE.dp

  val fallingFruits = remember { mutableStateListOf<FallingFruit>() }

  // Set a duration time if needed
  if (duration != Long.MIN_VALUE) {
    LaunchedEffect(duration) {
      delay(duration)
      onFinish()
    }
  }

  // Coroutine to add new fruits
  LaunchedEffect(Unit) {
    while (fallingFruits.size < NUMBER_FRUIT_MAX) {
      delay(DELAY_SPAWN)

      val xPos = getXPos(screenWidth.value, fruitSize.value)
      val newFruit =
          FallingFruit(
              imageRes = fruitImages.random(),
              initialPositionX = xPos,
              initialPositionY = -fruitSize.value,
              initialVelocity = INITIAL_SPEED * Random.nextFloat())
      fallingFruits.add(newFruit)
    }
  }

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    fallingFruits.forEachIndexed { index, fruit ->
      LaunchedEffect(fruit) {
        while (true) {
          fruit.fall(screenHeight.value) {
            fruit.setRandomFruit(screenWidth.value, fruitSize.value)
          }
        }
      }

      // Apply offset and rotation to each fruit image
      Image(
          painter = painterResource(id = fruit.imageRes),
          contentDescription = "Falling Fruit",
          modifier =
              Modifier.offset(x = fruit.positionX.dp, y = fruit.positionY.dp)
                  .size(fruitSize)
                  .graphicsLayer(rotationZ = fruit.rotation) // Apply rotation
                  .testTag("FruitImage_$index"), // Unique tag for each fruit
          contentScale = ContentScale.Fit)
    }
  }
}

/**
 * Get a random X position for the fruit
 *
 * @param screenWidth: Width of the screen
 * @param fruitSize: Size of the fruit
 */
private fun getXPos(screenWidth: Float, fruitSize: Float) =
    (Random.nextFloat() * (2 * UNIT) - UNIT) * (screenWidth / 2 - fruitSize / 2)
