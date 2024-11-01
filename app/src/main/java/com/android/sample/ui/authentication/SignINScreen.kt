package com.android.sample.ui.authentication

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import com.android.sample.R
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider.getCredential
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(navigationActions: NavigationActions) {
  val context = LocalContext.current

  val coroutineScope = rememberCoroutineScope()
  val activityContext = LocalContext.current
  val token = stringResource(R.string.default_web_client_id)

  val googleIdOption: GetGoogleIdOption =
      GetGoogleIdOption.Builder()
          .setFilterByAuthorizedAccounts(true)
          .setServerClientId(token)
          // .setAutoSelectEnabled(true)
          .setNonce("nonce")
          .build()

  val request: androidx.credentials.GetCredentialRequest =
      androidx.credentials.GetCredentialRequest.Builder()
          .addCredentialOption(googleIdOption)
          .build()

  val credentialManager = CredentialManager.create(activityContext)

  // The main container for the screen
  Scaffold(
      modifier = Modifier.fillMaxSize(),
      content = { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
          // App Logo with rounded corners
          Box(
              modifier =
                  Modifier.size(200.dp) // Increase the size of the logo
                      .clip(RoundedCornerShape(16.dp)) // Round the corners
              ) {
                Image(
                    painter = painterResource(id = R.drawable.plateswipe_logo), // Use the new logo
                    contentDescription = "PlateSwipe Logo",
                    modifier = Modifier.fillMaxSize().testTag("logoImage"),
                    contentScale = ContentScale.Crop)
              }

          Spacer(modifier = Modifier.height(16.dp))

          // Welcome Text
          Text(
              modifier = Modifier.testTag("loginTitle"),
              text = "Welcome",
              style =
                  MaterialTheme.typography.headlineLarge.copy(fontSize = 57.sp, lineHeight = 64.sp),
              fontWeight = FontWeight.Bold,
              textAlign = TextAlign.Center)

          Spacer(modifier = Modifier.height(48.dp))

          // Authenticate With Google Button
          GoogleSignInButton(
              onSignInClick = {
                coroutineScope.launch {
                  googleSignInRequest(
                      credentialManager, request, activityContext, context, navigationActions)
                }
              })
        }
      })
}

private suspend fun googleSignInRequest(
    credentialManager: CredentialManager,
    request: androidx.credentials.GetCredentialRequest,
    activityContext: Context,
    context: Context,
    navigationActions: NavigationActions
) {
  try {
    val result =
        credentialManager.getCredential(
            request = request,
            context = activityContext,
        )
    when (val credential = result.credential) {
      is CustomCredential -> {
        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
          try {
            // Use googleIdTokenCredential and extract id to validate and
            // authenticate on your server.
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

            val idToken = googleIdTokenCredential.idToken

            // Pass the idToken to Firebase to authenticate
            fireBaseRequest(idToken, context, navigationActions)
          } catch (e: GoogleIdTokenParsingException) {

            Log.e(TAG, "Received an invalid google id token response", e)
          }
        } else {
          // Catch any unrecognized credential type here.
          Log.e(TAG, "Unexpected type of credential")
        }
      }
      else -> {
        // Catch any unrecognized credential type here.
        Log.e(TAG, "Unexpected type of credential")
      }
    }
  } catch (e: androidx.credentials.exceptions.GetCredentialException) {
    // handleFailure(e)
  }
}

private fun fireBaseRequest(
    idToken: String,
    context: Context,
    navigationActions: NavigationActions
) {
  val firebaseCredential = getCredential(idToken, null)
  FirebaseAuth.getInstance().signInWithCredential(firebaseCredential).addOnCompleteListener { task
    ->
    if (task.isSuccessful) {
      // Successfully authenticated with Firebase
      Log.d(TAG, "signInWithCredential:success")
      Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
      navigationActions.navigateTo(Screen.SWIPE)
      // Handle the authenticated user object here
    } else {
      // Handle failures
      Toast.makeText(context, "Login failed!", Toast.LENGTH_SHORT).show()
      Log.e(TAG, "signInWithCredential:failure", task.exception)
    }
  }
}

@Composable
fun GoogleSignInButton(onSignInClick: () -> Unit) {

  Button(
      onClick = onSignInClick,
      colors = ButtonDefaults.buttonColors(containerColor = Color.White), // Button color
      shape = RoundedCornerShape(100), // Circular edges for the button
      border = BorderStroke(1.dp, Color.LightGray),
      modifier =
          Modifier.padding(8.dp)
              .height(48.dp) // Adjust height as needed
              .testTag("loginButton")) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()) {
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
}
