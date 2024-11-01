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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.android.sample.R
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.common.primitives.Floats.min
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider.getCredential
import kotlin.math.abs
import kotlin.math.sign
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

  val coroutineScope = rememberCoroutineScope()
  val activityContext = LocalContext.current
  val token = stringResource(R.string.default_web_client_id)

  /*val googleIdOption: GetGoogleIdOption =
  GetGoogleIdOption.Builder()
      .setFilterByAuthorizedAccounts(true)
      .setServerClientId(token)
      //.setAutoSelectEnabled(true)
      .setNonce("nonce")
      .build()*/
  val signInWithGoogleOption: GetSignInWithGoogleOption =
      GetSignInWithGoogleOption.Builder(token).setNonce("setupNonce").build()

  val request: GetCredentialRequest =
      GetCredentialRequest.Builder()
          // .addCredentialOption(googleIdOption)
          .addCredentialOption(signInWithGoogleOption)
          .build()

  // The main container for the screen
  Scaffold(
      modifier = Modifier.fillMaxSize(),
      content = { padding ->
        RecipesAnimation()

        SignInContent(
            padding,
            onSignInClick = {
              coroutineScope.launch(Dispatchers.Main) {
                googleSignInRequest(
                    onAuthComplete = { result ->
                      Log.d("SignInScreen", "User signed in: ${result.user?.displayName}")
                      Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                      navigationActions.navigateTo(Screen.SWIPE)
                    },
                    onAuthError = {
                      Log.e("SignInScreen", "Failed to sign in: ${it.message}")
                      Toast.makeText(context, "Login Failed!", Toast.LENGTH_SHORT).show()
                    },
                    request = request,
                    activityContext = activityContext)
              }
            })
      })
}

/** Group of animated images that move around the screen. */
@Composable
private fun RecipesAnimation() {
  Box(modifier = Modifier.fillMaxSize().zIndex(-1f), contentAlignment = Alignment.Center) {
    // Animated tacos around the cook image
    AnimatedImage(
        imageRes = R.drawable.taco,
        initialOffsetX = -(250).dp,
        initialOffsetY = 400.dp,
        rotationSpeed = 15,
        movementDuration = 1500,
        movementRange = 9f,
        testTag = "taco")
    AnimatedImage(
        imageRes = R.drawable.sushi,
        initialOffsetX = -(350).dp,
        initialOffsetY = 150.dp,
        rotationSpeed = 15,
        movementDuration = 1000,
        movementRange = 8f,
        testTag = "sushi")
    AnimatedImage(
        imageRes = R.drawable.avocado,
        initialOffsetX = -(350).dp,
        initialOffsetY = -(200).dp,
        rotationSpeed = 15,
        movementDuration = 1250,
        movementRange = 9f,
        testTag = "avocado")
    AnimatedImage(
        imageRes = R.drawable.tomato,
        initialOffsetX = -(180).dp,
        initialOffsetY = -(450).dp,
        rotationSpeed = 20,
        movementDuration = 1500,
        movementRange = 10f,
        testTag = "tomato")
    AnimatedImage(
        imageRes = R.drawable.pancakes,
        initialOffsetX = 200.dp,
        initialOffsetY = -(450).dp,
        rotationSpeed = 15,
        movementDuration = 800,
        movementRange = 6f,
        testTag = "pancakes")

    AnimatedImage(
        imageRes = R.drawable.broccoli,
        initialOffsetX = 330.dp,
        initialOffsetY = -(250).dp,
        rotationSpeed = 17,
        movementDuration = 1500,
        movementRange = 9f,
        testTag = "broccoli")

    AnimatedImage(
        imageRes = R.drawable.pasta,
        initialOffsetX = 345.dp,
        initialOffsetY = 0.dp,
        rotationSpeed = 10,
        movementDuration = 1200,
        movementRange = 12f,
        testTag = "pasta")

    AnimatedImage(
        imageRes = R.drawable.salad,
        initialOffsetX = 300.dp,
        initialOffsetY = 225.dp,
        rotationSpeed = 17,
        movementDuration = 900,
        movementRange = 9f,
        testTag = "salad")

    AnimatedImage(
        imageRes = R.drawable.pepper,
        initialOffsetX = 150.dp,
        initialOffsetY = 400.dp,
        rotationSpeed = 14,
        movementDuration = 1500,
        movementRange = 10f,
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
fun AnimatedImage(
    imageRes: Int,
    initialOffsetX: Dp,
    initialOffsetY: Dp,
    rotationSpeed: Int,
    movementDuration: Int,
    movementRange: Float,
    testTag: String
) {
  val width = LocalConfiguration.current.screenWidthDp.toFloat()
  val height = LocalConfiguration.current.screenHeightDp.toFloat()

  val ratioWidth = width / 360f
  val ratioHeight = height / 755f
  val iconSize = 70.dp * (ratioWidth + ratioHeight) / 2

  val infiniteTransition = rememberInfiniteTransition(label = "transition")

  // Animate rotation
  val rotationAngle by
      infiniteTransition.animateFloat(
          initialValue = -rotationSpeed.toFloat(),
          targetValue = rotationSpeed.toFloat(),
          animationSpec =
              infiniteRepeatable(
                  animation =
                      tween(durationMillis = 30000 / rotationSpeed, easing = FastOutSlowInEasing),
                  repeatMode = RepeatMode.Reverse),
          label = "rotation")

  // Movement animation using keyframes to return to start position
  val offsetX by
      infiniteTransition.animateFloat(
          // manage when images goes outside of the screen
          initialValue = sign(initialOffsetX.value) * min(abs(initialOffsetX.value), width),
          targetValue =
              sign(initialOffsetX.value + movementRange) *
                  min(abs(initialOffsetX.value + movementRange), width),
          animationSpec =
              infiniteRepeatable(
                  animation =
                      tween(
                          durationMillis = movementDuration,
                          easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1.0f)), // Smoother easing
                  repeatMode = RepeatMode.Reverse),
          label = "xTranslation")

  val offsetY by
      infiniteTransition.animateFloat(
          initialValue =
              sign(initialOffsetY.value) * min(abs(initialOffsetY.value), height - iconSize.value),
          targetValue =
              sign(initialOffsetY.value) *
                  min(abs(initialOffsetY.value + movementRange), height - iconSize.value),
          animationSpec =
              infiniteRepeatable(
                  animation =
                      tween(
                          durationMillis = movementDuration,
                          easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1.0f)), // Smoother easing
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
 * @param padding: Padding values
 * @param onSignInClick: Callback function when the sign-in button is clicked
 */
@Composable
private fun SignInContent(padding: PaddingValues, onSignInClick: () -> Unit) {
  Column(
      modifier = Modifier.fillMaxSize().padding(padding),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
  ) {
    Column(
        modifier = Modifier.testTag("loginTitle").weight(2f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
          Spacer(modifier = Modifier.height(16.dp))
          Text(
              text = "Plate",
              modifier = Modifier.testTag("plateText"),
              style =
                  TextStyle(
                      fontSize = 60.sp,
                      fontFamily = FontFamily(Font(R.font.montserrat_bold)),
                      fontWeight = FontWeight(600),
                      color = Color.Black,
                      textAlign = TextAlign.Center,
                  ),
              textAlign = TextAlign.Center)
          Row(modifier = Modifier) {
            Spacer(modifier = Modifier.width(50.dp))
            Text(
                text = "Swipe",
                modifier = Modifier.testTag("swipeText"),
                style =
                    TextStyle(
                        fontSize = 60.sp,
                        fontFamily = FontFamily(Font(R.font.montserrat_bold)),
                        fontWeight = FontWeight(600),
                        color = Color(0xFF000000),
                        textAlign = TextAlign.Center,
                    ),
                textAlign = TextAlign.Center)
          }
        }

    // App Logo with rounded corners
    Box(
        modifier =
            Modifier.fillMaxSize()
                .size(300.dp) // Increase the size of the logo
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
    handleSignIn(credentialManager, request, activityContext, onAuthComplete, onAuthError)
  } catch (e: androidx.credentials.exceptions.GetCredentialException) {
    onAuthError(e)
  }
}

/**
 * Function to handle the sign-in process. Request firebase credential and authenticate.
 *
 * @param credentialManager: CredentialManager object
 * @param request: GetCredentialRequest object
 * @param activityContext: Context of the activity
 * @param onAuthComplete: Callback function when authentication is successful
 * @param onAuthError: Callback function when authentication fails
 */
private suspend fun handleSignIn(
    credentialManager: CredentialManager,
    request: GetCredentialRequest,
    activityContext: Context,
    onAuthComplete: (AuthResult) -> Unit,
    onAuthError: (Exception) -> Unit
) {
  val result =
      credentialManager.getCredential(
          request = request,
          context = activityContext,
      )
  when (val credential = result.credential) {
    // Passkey credential
    is CustomCredential -> {
      if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        try {
          // Use googleIdTokenCredential and extract id to validate and
          // authenticate on your server.
          val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

          val idToken = googleIdTokenCredential.idToken

          // Pass the idToken to Firebase to authenticate
          val firebaseCredential = getCredential(idToken, null)
          val authResult =
              FirebaseAuth.getInstance().signInWithCredential(firebaseCredential).await()
          onAuthComplete(authResult)
        } catch (e: GoogleIdTokenParsingException) {
          onAuthError(e)
        }
      }
    }
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
      colors = ButtonDefaults.buttonColors(containerColor = Color.White), // Button color
      shape = RoundedCornerShape(100), // Circular edges for the button
      border = BorderStroke(1.dp, Color.LightGray),
      modifier = Modifier.padding(16.dp).height(48.dp).testTag("loginButton")) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().weight(1f)) {
              // Load the Google logo from resources
              Image(
                  painter = painterResource(id = R.drawable.google_logo),
                  contentDescription = "Google Logo",
                  modifier = Modifier.size(30.dp).padding(end = 8.dp))

              // Text for the button
              Text(
                  text = "Sign in with Google",
                  color = Color.Gray, // Text color
                  fontSize = 16.sp, // Font size
                  fontWeight = FontWeight.Medium)
            }
      }
  Spacer(modifier = Modifier.height(32.dp))
}
