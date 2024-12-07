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
import com.android.sample.animation.LoadingAnimation
import com.android.sample.model.user.UserViewModel
import com.android.sample.resources.C.Dimension.SignInScreen.AVOCADO_MVM_DURATION
import com.android.sample.resources.C.Dimension.SignInScreen.AVOCADO_MVM_RANGE
import com.android.sample.resources.C.Dimension.SignInScreen.AVOCADO_ROTATION
import com.android.sample.resources.C.Dimension.SignInScreen.AVOCADO_X
import com.android.sample.resources.C.Dimension.SignInScreen.AVOCADO_Y
import com.android.sample.resources.C.Dimension.SignInScreen.BROCCOLI_MVM_DURATION
import com.android.sample.resources.C.Dimension.SignInScreen.BROCCOLI_MVM_RANGE
import com.android.sample.resources.C.Dimension.SignInScreen.BROCCOLI_ROTATION
import com.android.sample.resources.C.Dimension.SignInScreen.BROCCOLI_X
import com.android.sample.resources.C.Dimension.SignInScreen.BROCCOLI_Y
import com.android.sample.resources.C.Dimension.SignInScreen.COOK_SIZE
import com.android.sample.resources.C.Dimension.SignInScreen.CUBE_EASING_A
import com.android.sample.resources.C.Dimension.SignInScreen.CUBE_EASING_B
import com.android.sample.resources.C.Dimension.SignInScreen.CUBE_EASING_C
import com.android.sample.resources.C.Dimension.SignInScreen.CUBE_EASING_D
import com.android.sample.resources.C.Dimension.SignInScreen.DURATION_ROTATION_LOOP
import com.android.sample.resources.C.Dimension.SignInScreen.GOOGLE_LOGO_SIZE
import com.android.sample.resources.C.Dimension.SignInScreen.HEIGHT_BASE
import com.android.sample.resources.C.Dimension.SignInScreen.ORIGINAL_ICON_SIZE
import com.android.sample.resources.C.Dimension.SignInScreen.PANCAKES_MVM_DURATION
import com.android.sample.resources.C.Dimension.SignInScreen.PANCAKES_MVM_RANGE
import com.android.sample.resources.C.Dimension.SignInScreen.PANCAKES_ROTATION
import com.android.sample.resources.C.Dimension.SignInScreen.PANCAKES_X
import com.android.sample.resources.C.Dimension.SignInScreen.PANCAKES_Y
import com.android.sample.resources.C.Dimension.SignInScreen.PASTA_MVM_DURATION
import com.android.sample.resources.C.Dimension.SignInScreen.PASTA_MVM_RANGE
import com.android.sample.resources.C.Dimension.SignInScreen.PASTA_ROTATION
import com.android.sample.resources.C.Dimension.SignInScreen.PASTA_X
import com.android.sample.resources.C.Dimension.SignInScreen.PASTA_Y
import com.android.sample.resources.C.Dimension.SignInScreen.PEPPER_MVM_DURATION
import com.android.sample.resources.C.Dimension.SignInScreen.PEPPER_MVM_RANGE
import com.android.sample.resources.C.Dimension.SignInScreen.PEPPER_ROTATION
import com.android.sample.resources.C.Dimension.SignInScreen.PEPPER_X
import com.android.sample.resources.C.Dimension.SignInScreen.PEPPER_Y
import com.android.sample.resources.C.Dimension.SignInScreen.SALAD_MVM_DURATION
import com.android.sample.resources.C.Dimension.SignInScreen.SALAD_MVM_RANGE
import com.android.sample.resources.C.Dimension.SignInScreen.SALAD_ROTATION
import com.android.sample.resources.C.Dimension.SignInScreen.SALAD_X
import com.android.sample.resources.C.Dimension.SignInScreen.SALAD_Y
import com.android.sample.resources.C.Dimension.SignInScreen.SUSHI_MVM_DURATION
import com.android.sample.resources.C.Dimension.SignInScreen.SUSHI_MVM_RANGE
import com.android.sample.resources.C.Dimension.SignInScreen.SUSHI_ROTATION
import com.android.sample.resources.C.Dimension.SignInScreen.SUSHI_X
import com.android.sample.resources.C.Dimension.SignInScreen.SUSHI_Y
import com.android.sample.resources.C.Dimension.SignInScreen.TACO_MVM_DURATION
import com.android.sample.resources.C.Dimension.SignInScreen.TACO_MVM_RANGE
import com.android.sample.resources.C.Dimension.SignInScreen.TACO_ROTATION
import com.android.sample.resources.C.Dimension.SignInScreen.TACO_X
import com.android.sample.resources.C.Dimension.SignInScreen.TACO_Y
import com.android.sample.resources.C.Dimension.SignInScreen.TOMATO_MVM_DURATION
import com.android.sample.resources.C.Dimension.SignInScreen.TOMATO_MVM_RANGE
import com.android.sample.resources.C.Dimension.SignInScreen.TOMATO_ROTATION
import com.android.sample.resources.C.Dimension.SignInScreen.TOMATO_X
import com.android.sample.resources.C.Dimension.SignInScreen.TOMATO_Y
import com.android.sample.resources.C.Dimension.SignInScreen.WIDTH_BASE
import com.android.sample.resources.C.Tag.PADDING
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.Tag.SignInScreen.ANIMATION_DURATION
import com.android.sample.resources.C.Tag.SignInScreen.COOK_DESCRIPTION
import com.android.sample.resources.C.Tag.SignInScreen.GOOGLE_DESCRIPTION
import com.android.sample.resources.C.Tag.SignInScreen.LOGIN_FAILED
import com.android.sample.resources.C.Tag.SignInScreen.LOGIN_SUCCESSFUL
import com.android.sample.resources.C.Tag.SignInScreen.PLATE
import com.android.sample.resources.C.Tag.SignInScreen.ROTATION_LABEL
import com.android.sample.resources.C.Tag.SignInScreen.SHIFTING_SPACE_TITLE
import com.android.sample.resources.C.Tag.SignInScreen.SIGN_IN_ERROR_NO_CRED
import com.android.sample.resources.C.Tag.SignInScreen.SIGN_IN_FAILED
import com.android.sample.resources.C.Tag.SignInScreen.SIGN_IN_SUCCESSED
import com.android.sample.resources.C.Tag.SignInScreen.SIGN_IN_TAG
import com.android.sample.resources.C.Tag.SignInScreen.SIGN_IN_WITH_GOOGLE
import com.android.sample.resources.C.Tag.SignInScreen.SWIPE
import com.android.sample.resources.C.Tag.SignInScreen.TRANSITION_LABEL
import com.android.sample.resources.C.Tag.SignInScreen.X_TRANSLATION_LABEL
import com.android.sample.resources.C.Tag.SignInScreen.Y_TRANSLATION_LABEL
import com.android.sample.resources.C.TestTag.SignInScreen.AVOCADO
import com.android.sample.resources.C.TestTag.SignInScreen.BROCCOLI
import com.android.sample.resources.C.TestTag.SignInScreen.COOK_IMAGE
import com.android.sample.resources.C.TestTag.SignInScreen.LOGIN_BUTTON
import com.android.sample.resources.C.TestTag.SignInScreen.LOGIN_TITLE
import com.android.sample.resources.C.TestTag.SignInScreen.PANCAKES
import com.android.sample.resources.C.TestTag.SignInScreen.PASTA
import com.android.sample.resources.C.TestTag.SignInScreen.PEPPER
import com.android.sample.resources.C.TestTag.SignInScreen.PLATE_TEXT
import com.android.sample.resources.C.TestTag.SignInScreen.SALAD
import com.android.sample.resources.C.TestTag.SignInScreen.SUSHI
import com.android.sample.resources.C.TestTag.SignInScreen.SWIPE_TEXT
import com.android.sample.resources.C.TestTag.SignInScreen.TACO
import com.android.sample.resources.C.TestTag.SignInScreen.TOMATO
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider.getCredential
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Sign-in screen for the app. Users can sign in with Google.
 *
 * @param navigationActions: NavigationActions object
 */
@Composable
fun SignInScreen(navigationActions: NavigationActions, userViewModel: UserViewModel) {
  val context = LocalContext.current
  val registered = remember { mutableStateOf(false) }
  var isProcessing by remember { mutableStateOf(false) }

  val coroutineScope = rememberCoroutineScope()
  val activityContext = LocalContext.current
  val token = stringResource(R.string.default_web_client_id)

  val getSignInWithGoogleOption: GetSignInWithGoogleOption =
      GetSignInWithGoogleOption.Builder(token).build()

  val request: GetCredentialRequest =
      GetCredentialRequest.Builder().addCredentialOption(getSignInWithGoogleOption).build()

  // The main container for the screen
  Scaffold(
      modifier = Modifier.fillMaxSize(),
      content = { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
          // Show the loading animation if the user is registered
          if (Firebase.auth.currentUser != null || registered.value) {
            Log.d("SignIn", "User is registered")

            LoadingAnimation(
                onFinish = {
                  userViewModel.getCurrentUser(context)
                  navigationActions.navigateTo(Screen.SWIPE)
                },
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
                            Log.d(SIGN_IN_TAG, "$SIGN_IN_SUCCESSED${result.user?.displayName}")
                            Toast.makeText(context, LOGIN_SUCCESSFUL, Toast.LENGTH_SHORT).show()
                            registered.value = true
                            isProcessing = false
                          },
                          onAuthError = {
                            Log.e(SIGN_IN_TAG, "$SIGN_IN_FAILED${it.message}")
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
        testTag = TACO)
    AnimatedImage(
        imageRes = R.drawable.sushi,
        initialOffsetX = SUSHI_X.dp,
        initialOffsetY = SUSHI_Y.dp,
        rotationSpeed = SUSHI_ROTATION,
        movementDuration = SUSHI_MVM_DURATION,
        movementRange = SUSHI_MVM_RANGE,
        testTag = SUSHI)
    AnimatedImage(
        imageRes = R.drawable.avocado,
        initialOffsetX = AVOCADO_X.dp,
        initialOffsetY = AVOCADO_Y.dp,
        rotationSpeed = AVOCADO_ROTATION,
        movementDuration = AVOCADO_MVM_DURATION,
        movementRange = AVOCADO_MVM_RANGE,
        testTag = AVOCADO)
    AnimatedImage(
        imageRes = R.drawable.tomato,
        initialOffsetX = TOMATO_X.dp,
        initialOffsetY = TOMATO_Y.dp,
        rotationSpeed = TOMATO_ROTATION,
        movementDuration = TOMATO_MVM_DURATION,
        movementRange = TOMATO_MVM_RANGE,
        testTag = TOMATO)
    AnimatedImage(
        imageRes = R.drawable.pancakes,
        initialOffsetX = PANCAKES_X.dp,
        initialOffsetY = PANCAKES_Y.dp,
        rotationSpeed = PANCAKES_ROTATION,
        movementDuration = PANCAKES_MVM_DURATION,
        movementRange = PANCAKES_MVM_RANGE,
        testTag = PANCAKES)

    AnimatedImage(
        imageRes = R.drawable.broccoli,
        initialOffsetX = BROCCOLI_X.dp,
        initialOffsetY = BROCCOLI_Y.dp,
        rotationSpeed = BROCCOLI_ROTATION,
        movementDuration = BROCCOLI_MVM_DURATION,
        movementRange = BROCCOLI_MVM_RANGE,
        testTag = BROCCOLI)

    AnimatedImage(
        imageRes = R.drawable.pasta,
        initialOffsetX = PASTA_X.dp,
        initialOffsetY = PASTA_Y.dp,
        rotationSpeed = PASTA_ROTATION,
        movementDuration = PASTA_MVM_DURATION,
        movementRange = PASTA_MVM_RANGE,
        testTag = PASTA)

    AnimatedImage(
        imageRes = R.drawable.salad,
        initialOffsetX = SALAD_X.dp,
        initialOffsetY = SALAD_Y.dp,
        rotationSpeed = SALAD_ROTATION,
        movementDuration = SALAD_MVM_DURATION,
        movementRange = SALAD_MVM_RANGE,
        testTag = SALAD)

    AnimatedImage(
        imageRes = R.drawable.pepper,
        initialOffsetX = PEPPER_X.dp,
        initialOffsetY = PEPPER_Y.dp,
        rotationSpeed = PEPPER_ROTATION,
        movementDuration = PEPPER_MVM_DURATION,
        movementRange = PEPPER_MVM_RANGE,
        testTag = PEPPER)
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

  val infiniteTransition = rememberInfiniteTransition(label = TRANSITION_LABEL)

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
          label = ROTATION_LABEL)

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
          label = X_TRANSLATION_LABEL)

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
          label = Y_TRANSLATION_LABEL)

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
          contentDescription = COOK_DESCRIPTION,
          modifier = Modifier.fillMaxSize().testTag(COOK_IMAGE),
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
      modifier = modifier.fillMaxWidth().testTag(LOGIN_TITLE),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
        Spacer(modifier = Modifier.height(PADDING.dp))
        Row(modifier = Modifier) {
          Text(
              text = PLATE,
              modifier = Modifier.testTag(PLATE_TEXT),
              style = MaterialTheme.typography.titleLarge,
              color = MaterialTheme.colorScheme.onPrimary,
              textAlign = TextAlign.Center)
          Spacer(modifier = Modifier.width(SHIFTING_SPACE_TITLE.dp))
        }

        Row {
          Spacer(modifier = Modifier.width(SHIFTING_SPACE_TITLE.dp))
          Text(
              text = SWIPE,
              modifier = Modifier.testTag(SWIPE_TEXT),
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
          onAuthError(Exception(SIGN_IN_ERROR_NO_CRED))
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
          Modifier.padding(horizontal = (SMALL_PADDING).dp, vertical = (SMALL_PADDING / 4).dp)
              .wrapContentSize()
              .testTag(LOGIN_BUTTON)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().weight(1f)) {
              // Load the Google logo from resources
              Image(
                  painter = painterResource(id = R.drawable.google_logo),
                  contentDescription = GOOGLE_DESCRIPTION,
                  modifier = Modifier.size(GOOGLE_LOGO_SIZE.dp).padding(end = SMALL_PADDING.dp))

              // Text for the button
              Text(
                  text = SIGN_IN_WITH_GOOGLE,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSecondary,
              )
            }
      }
  Spacer(modifier = Modifier.height((PADDING * 2).dp))
}
