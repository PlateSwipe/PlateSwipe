package com.android.sample.ui.testScreens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
fun AuthScreen() {
  Scaffold(modifier = Modifier.testTag("authScreen")) {
    Text(text = "authScreenText", modifier = Modifier.padding(it))
  }
}
