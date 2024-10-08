package com.android.sample.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.android.sample.ui.navigation.Screen

@Composable
fun OverviewScreen(navController: NavController) {
  Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
        Text("Hello from OverviewScreen Compose!")
        Button(onClick = { navController.navigate(Screen.CAMERA) }) { Text("Open Camera") }
      }
}
