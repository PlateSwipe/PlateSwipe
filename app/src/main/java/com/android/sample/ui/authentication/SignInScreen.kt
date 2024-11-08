package com.android.sample.ui.authentication

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.android.sample.R
import com.android.sample.resources.C.Tag.ANIMATION_DURATION
import com.android.sample.resources.C.Tag.AVOCADO_MVM_DURATION
import com.android.sample.resources.C.Tag.AVOCADO_MVM_RANGE
import com.android.sample.resources.C.Tag.AVOCADO_ROTATION
import com.android.sample.resources.C.Tag.AVOCADO_X
import com.android.sample.resources.C.Tag.AVOCADO_Y
import com.android.sample.resources.C.Tag.BROCCOLI_MVM_DURATION
import com.android.sample.resources.C.Tag.BROCCOLI_MVM_RANGE
import com.android.sample.resources.C.Tag.BROCCOLI_ROTATION
import com.android.sample.resources.C.Tag.BROCCOLI_X
import com.android.sample.resources.C.Tag.BROCCOLI_Y
import com.android.sample.resources.C.Tag.COOK_SIZE
import com.android.sample.resources.C.Tag.CUBE_EASING_A
import com.android.sample.resources.C.Tag.CUBE_EASING_B
import com.android.sample.resources.C.Tag.CUBE_EASING_C
import com.android.sample.resources.C.Tag.CUBE_EASING_D
import com.android.sample.resources.C.Tag.DURATION_ROTATION_LOOP
import com.android.sample.resources.C.Tag.GOOGLE_LOGO_SIZE
import com.android.sample.resources.C.Tag.HEIGHT_BASE
import com.android.sample.resources.C.Tag.LOGIN_FAILED
import com.android.sample.resources.C.Tag.LOGIN_SUCCESSFUL
import com.android.sample.resources.C.Tag.NONCE
import com.android.sample.resources.C.Tag.ORIGINAL_ICON_SIZE
import com.android.sample.resources.C.Tag.PANCAKES_MVM_DURATION
import com.android.sample.resources.C.Tag.PANCAKES_MVM_RANGE
import com.android.sample.resources.C.Tag.PANCAKES_ROTATION
import com.android.sample.resources.C.Tag.PANCAKES_X
import com.android.sample.resources.C.Tag.PANCAKES_Y
import com.android.sample.resources.C.Tag.PASTA_MVM_DURATION
import com.android.sample.resources.C.Tag.PASTA_MVM_RANGE
import com.android.sample.resources.C.Tag.PASTA_ROTATION
import com.android.sample.resources.C.Tag.PASTA_X
import com.android.sample.resources.C.Tag.PASTA_Y
import com.android.sample.resources.C.Tag.PEPPER_MVM_DURATION
import com.android.sample.resources.C.Tag.PEPPER_MVM_RANGE
import com.android.sample.resources.C.Tag.PEPPER_ROTATION
import com.android.sample.resources.C.Tag.PEPPER_X
import com.android.sample.resources.C.Tag.PEPPER_Y
import com.android.sample.resources.C.Tag.PLATE
import com.android.sample.resources.C.Tag.SALAD_MVM_DURATION
import com.android.sample.resources.C.Tag.SALAD_MVM_RANGE
import com.android.sample.resources.C.Tag.SALAD_ROTATION
import com.android.sample.resources.C.Tag.SALAD_X
import com.android.sample.resources.C.Tag.SALAD_Y
import com.android.sample.resources.C.Tag.SHIFTING_SPACE_TITLE
import com.android.sample.resources.C.Tag.SIGN_IN_WITH_GOOGLE
import com.android.sample.resources.C.Tag.SPACE
import com.android.sample.resources.C.Tag.SUSHI_MVM_DURATION
import com.android.sample.resources.C.Tag.SUSHI_MVM_RANGE
import com.android.sample.resources.C.Tag.SUSHI_ROTATION
import com.android.sample.resources.C.Tag.SUSHI_X
import com.android.sample.resources.C.Tag.SUSHI_Y
import com.android.sample.resources.C.Tag.SWIPE
import com.android.sample.resources.C.Tag.TACO_MVM_DURATION
import com.android.sample.resources.C.Tag.TACO_MVM_RANGE
import com.android.sample.resources.C.Tag.TACO_ROTATION
import com.android.sample.resources.C.Tag.TACO_X
import com.android.sample.resources.C.Tag.TACO_Y
import com.android.sample.resources.C.Tag.TOMATO_MVM_DURATION
import com.android.sample.resources.C.Tag.TOMATO_MVM_RANGE
import com.android.sample.resources.C.Tag.TOMATO_ROTATION
import com.android.sample.resources.C.Tag.TOMATO_X
import com.android.sample.resources.C.Tag.TOMATO_Y
import com.android.sample.resources.C.Tag.WIDTH_BASE
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.utils.LoadingAnimation
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider.getCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Sign-in screen for the app. Users can sign in with Google.
 *
 * @param navigationActions: NavigationActions object
 */
@Composable
fun SignInScreen(navigationActions: NavigationActions) {
  val context = LocalContext.current
  val registered = remember { mutableStateOf(false) }
  var isProcessing by remember { mutableStateOf(false) }

  val coroutineScope = rememberCoroutineScope()
  val activityContext = LocalContext.current
  val token = stringResource(R.string.default_web_client_id)

  val googleIdOption: GetGoogleIdOption =
      GetGoogleIdOption.Builder()
          .setFilterByAuthorizedAccounts(true)
          .setServerClientId(token)
          .setAutoSelectEnabled(true)
          .setNonce(NONCE)
          .build()

  val request: GetCredentialRequest =
      GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

  // The main container for the screen
  Scaffold(
      modifier = Modifier.fillMaxSize(),
      content = { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
          // Show the loading animation if the user is registered
          if (registered.value) {
            LoadingAnimation(
                onFinish = { navigationActions.navigateTo(Screen.SWIPE) },
                duration = ANIMATION_DURATION)
          } else {
            RecipesAnimation()

            SignInContent(
                onSignInClick = {
                  if (!isProcessing) {
                    isProcessing = true
                    // force runing on main thread
                    coroutineScope.launch(Dispatchers.Main) {
                      googleSignInRequest(
                          onAuthComplete = { result ->
                            Log.d("SignInScreen", "User signed in: ${result.user?.displayName}")
                            Toast.makeText(context, LOGIN_SUCCESSFUL, Toast.LENGTH_SHORT).show()
                            registered.value = true
                            isProcessing = false
                          },
                          onAuthError = {
                            Log.e("SignInScreen", "Failed to sign in: ${it.message}")
                            Toast.makeText(context, LOGIN_FAILED, Toast.LENGTH_SHORT).show()
                            isProcessing = false
                          },
                          request = request,
                          activityContext = activityContext)
                    }
                  }
                })
          }
        }
      })
}

/** Group of animated images that move around the screen. */
@Composable
private fun RecipesAnimation() {
  Box(modifier = Modifier.fillMaxSize().zIndex(-1f), contentAlignment = Alignment.Center) {
    // Animated tacos around the cook image
    AnimatedImage(
        imageRes = R.drawable.taco,
        initialOffsetX = TACO_X.dp,
        initialOffsetY = TACO_Y.dp,
        rotationSpeed = TACO_ROTATION,
        movementDuration = TACO_MVM_DURATION,
        movementRange = TACO_MVM_RANGE,
        testTag = "taco")
    AnimatedImage(
        imageRes = R.drawable.sushi,
        initialOffsetX = SUSHI_X.dp,
        initialOffsetY = SUSHI_Y.dp,
        rotationSpeed = SUSHI_ROTATION,
        movementDuration = SUSHI_MVM_DURATION,
        movementRange = SUSHI_MVM_RANGE,
        testTag = "sushi")
    AnimatedImage(
        imageRes = R.drawable.avocado,
        initialOffsetX = AVOCADO_X.dp,
        initialOffsetY = AVOCADO_Y.dp,
        rotationSpeed = AVOCADO_ROTATION,
        movementDuration = AVOCADO_MVM_DURATION,
        movementRange = AVOCADO_MVM_RANGE,
        testTag = "avocado")
    AnimatedImage(
        imageRes = R.drawable.tomato,
        initialOffsetX = TOMATO_X.dp,
        initialOffsetY = TOMATO_Y.dp,
        rotationSpeed = TOMATO_ROTATION,
        movementDuration = TOMATO_MVM_DURATION,
        movementRange = TOMATO_MVM_RANGE,
        testTag = "tomato")
    AnimatedImage(
        imageRes = R.drawable.pancakes,
        initialOffsetX = PANCAKES_X.dp,
        initialOffsetY = PANCAKES_Y.dp,
        rotationSpeed = PANCAKES_ROTATION,
        movementDuration = PANCAKES_MVM_DURATION,
        movementRange = PANCAKES_MVM_RANGE,
        testTag = "pancakes")

    AnimatedImage(
        imageRes = R.drawable.broccoli,
        initialOffsetX = BROCCOLI_X.dp,
        initialOffsetY = BROCCOLI_Y.dp,
        rotationSpeed = BROCCOLI_ROTATION,
        movementDuration = BROCCOLI_MVM_DURATION,
        movementRange = BROCCOLI_MVM_RANGE,
        testTag = "broccoli")

    AnimatedImage(
        imageRes = R.drawable.pasta,
        initialOffsetX = PASTA_X.dp,
        initialOffsetY = PASTA_Y.dp,
        rotationSpeed = PASTA_ROTATION,
        movementDuration = PASTA_MVM_DURATION,
        movementRange = PASTA_MVM_RANGE,
        testTag = "pasta")

    AnimatedImage(
        imageRes = R.drawable.salad,
        initialOffsetX = SALAD_X.dp,
        initialOffsetY = SALAD_Y.dp,
        rotationSpeed = SALAD_ROTATION,
        movementDuration = SALAD_MVM_DURATION,
        movementRange = SALAD_MVM_RANGE,
        testTag = "salad")

    AnimatedImage(
        imageRes = R.drawable.pepper,
        initialOffsetX = PEPPER_X.dp,
        initialOffsetY = PEPPER_Y.dp,
        rotationSpeed = PEPPER_ROTATION,
        movementDuration = PEPPER_MVM_DURATION,
        movementRange = PEPPER_MVM_RANGE,
        testTag = "pepper")
  }
}

/**
 * Animated image that moves around the screen.
 *
 * @param imageRes: Image resource
 * @param initialOffsetX: Initial X offset
 * @param initialOffsetY: Initial Y offset
 * @param rotationSpeed: Speed of rotation
 * @param movementDuration: Duration of movement
 * @param movementRange: Range of movement
 * @param testTag: Test tag for the image
 */
@Composable
private fun AnimatedImage(
    imageRes: Int,
    initialOffsetX: Dp,
    initialOffsetY: Dp,
    rotationSpeed: Int,
    movementDuration: Int,
    movementRange: Float,
    testTag: String
) {
  val density = LocalDensity.current.density
  val width = LocalConfiguration.current.screenWidthDp * density
  val height = LocalConfiguration.current.screenHeightDp * density

  val ratioWidth = width / WIDTH_BASE
  val ratioHeight = height / HEIGHT_BASE
  val iconSize = ORIGINAL_ICON_SIZE.dp * (ratioWidth + ratioHeight) / 2

  val infiniteTransition = rememberInfiniteTransition(label = "transition")

  // Animate rotation
  val rotationAngle by
      infiniteTransition.animateFloat(
          initialValue = -rotationSpeed.toFloat(),
          targetValue = rotationSpeed.toFloat(),
          animationSpec =
              infiniteRepeatable(
                  animation =
                      tween(
                          durationMillis = DURATION_ROTATION_LOOP / rotationSpeed,
                          easing = FastOutSlowInEasing),
                  repeatMode = RepeatMode.Reverse),
          label = "rotation")

  // Movement animation using keyframes to return to start position
  val offsetX by
      infiniteTransition.animateFloat(
          // manage when images goes outside of the screen
          initialValue = initialOffsetX.value * ratioWidth,
          targetValue = (initialOffsetX.value + movementRange) * ratioWidth,
          animationSpec =
              infiniteRepeatable(
                  animation =
                      tween(
                          durationMillis = movementDuration,
                          easing =
                              CubicBezierEasing(
                                  CUBE_EASING_A, CUBE_EASING_B, CUBE_EASING_C, CUBE_EASING_D)),
                  repeatMode = RepeatMode.Reverse),
          label = "xTranslation")

  // manage vertical movement
  val offsetY by
      infiniteTransition.animateFloat(
          initialValue = initialOffsetY.value * ratioHeight,
          targetValue = (initialOffsetY.value + movementRange) * ratioHeight,
          animationSpec =
              infiniteRepeatable(
                  animation =
                      tween(
                          durationMillis = movementDuration,
                          easing =
                              CubicBezierEasing(
                                  CUBE_EASING_A,
                                  CUBE_EASING_B,
                                  CUBE_EASING_C,
                                  CUBE_EASING_D)), // Smoother easing
                  repeatMode = RepeatMode.Reverse),
          label = "yTranslation")

  Image(
      painter = painterResource(id = imageRes),
      contentDescription = null,
      modifier =
          Modifier.testTag(testTag)
              .size(iconSize) // Smaller size for the animated tacos
              .graphicsLayer(translationX = offsetX.dp.value, translationY = offsetY.dp.value)
              .rotate(rotationAngle))
}

/**
 * Main content of the sign-in screen. Displays the app logo, the cook and sign-in button.
 *
 * @param onSignInClick: Callback function when the sign-in button is clicked
 */
@Composable
private fun SignInContent(onSignInClick: () -> Unit) {
  Column(
      modifier = Modifier.fillMaxSize().padding(32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
  ) {
    PlateSwipeTitle(Modifier.weight(3f))

    // Cook image display
    Box(
        modifier =
            Modifier.fillMaxSize()
                .size(COOK_SIZE.dp)
                .clip(RoundedCornerShape(16.dp))
                .weight(5f), // Round the corners
    ) {
      Image(
          painter = painterResource(id = R.drawable.cook), // Use the new logo
          contentDescription = "cook image",
          modifier = Modifier.fillMaxSize().testTag("cookImage"),
          contentScale = ContentScale.Crop)
    }

    // Authenticate With Google Button
    GoogleSignInButton(onSignInClick = onSignInClick)
  }
}

/**
 * Title for the sign-in screen
 *
 * @param modifier: Modifier to specify the weight
 */
@Composable
private fun PlateSwipeTitle(modifier: Modifier = Modifier) {
  Column(
      modifier = modifier.fillMaxWidth().testTag("loginTitle"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
        Spacer(modifier = Modifier.height(SPACE.dp))
        Row(modifier = Modifier) {
          Text(
              text = PLATE,
              modifier = Modifier.testTag("plateText"),
              style = MaterialTheme.typography.titleLarge,
              color = MaterialTheme.colorScheme.onPrimary,
              textAlign = TextAlign.Center)
          Spacer(modifier = Modifier.width(SHIFTING_SPACE_TITLE.dp))
        }

        Row {
          Spacer(modifier = Modifier.width(SHIFTING_SPACE_TITLE.dp))
          Text(
              text = SWIPE,
              modifier = Modifier.testTag("swipeText"),
              style = MaterialTheme.typography.titleLarge,
              color = MaterialTheme.colorScheme.onPrimary,
              textAlign = TextAlign.Center)
        }
      }
}

/**
 * Function to handle Google Sign-In request
 *
 * @param onAuthComplete: Callback function when authentication is successful
 * @param onAuthError: Callback function when authentication fails
 * @param request: GetCredentialRequest object
 * @param activityContext: Context of the activity
 */
private suspend fun googleSignInRequest(
    onAuthComplete: (AuthResult) -> Unit,
    onAuthError: (Exception) -> Unit,
    request: GetCredentialRequest,
    activityContext: Context,
) {

  try {
    val credentialManager = CredentialManager.create(activityContext)
    val result =
        credentialManager.getCredential(
            request = request,
            context = activityContext,
        )
    when (val credential = result.credential) {
      // Passkey credential
      is CustomCredential -> {
        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {

          // Use googleIdTokenCredential and extract id to validate and
          // authenticate on your server.
          val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

          val idToken = googleIdTokenCredential.idToken

          // Pass the idToken to Firebase to authenticate
          val firebaseCredential = getCredential(idToken, null)
          val authResult =
              FirebaseAuth.getInstance().signInWithCredential(firebaseCredential).await()
          onAuthComplete(authResult)
        } else {
          onAuthError(Exception("Invalid credential type"))
        }
      }
    }
  } catch (e: Exception) {
    onAuthError(e)
  }
}

/**
 * Google Sign-In Button
 *
 * @param onSignInClick: Callback function when the button is clicked
 */
@Composable
fun GoogleSignInButton(onSignInClick: () -> Unit) {
  Button(
      onClick = onSignInClick,
      colors =
          ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.background,
              contentColor = MaterialTheme.colorScheme.onPrimary),
      elevation = ButtonDefaults.buttonElevation(4.dp),
      shape = RoundedCornerShape(100),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),
      modifier =
          Modifier.padding(horizontal = (SPACE / 2).dp, vertical = (SPACE / 4).dp)
              .wrapContentSize()
              .testTag("loginButton")) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().weight(1f)) {
              // Load the Google logo from resources
              Image(
                  painter = painterResource(id = R.drawable.google_logo),
                  contentDescription = "Google Logo",
                  modifier = Modifier.size(GOOGLE_LOGO_SIZE.dp).padding(end = (SPACE / 2).dp))

              // Text for the button
              Text(
                  text = SIGN_IN_WITH_GOOGLE,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSecondary,
              )
            }
      }
  Spacer(modifier = Modifier.height((SPACE * 2).dp))
}
