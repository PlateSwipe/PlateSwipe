package com.android.sample.ui.testScreens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
fun IngredientScreen() {
  Scaffold(
      modifier = Modifier.testTag("ingredientScreen"),
      content = { Text(text = "ingredientScreenText", modifier = Modifier.padding(it)) })
}
