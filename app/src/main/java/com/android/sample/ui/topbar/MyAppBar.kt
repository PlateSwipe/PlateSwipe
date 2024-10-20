package com.android.sample.ui.topbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.ui.theme.lightCream

/**
 * A composable function that displays a custom top app bar with a title and a back button.
 *
 * @param title The title to be displayed in the app bar. Defaults to "PlateSwipe".
 * @param onBackClick A lambda function to be executed when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppBar(title: String = "PlateSwipe", onBackClick: () -> Unit) {
  TopAppBar(
      title = {
        Row(
            modifier = Modifier.fillMaxWidth().testTag("AppBarRow"),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
              // Chef's Hat Icon
              Image(
                  painter = painterResource(id = R.drawable.chef_s_hat),
                  contentDescription = "Chef's hat",
                  modifier =
                      Modifier.width(24.dp)
                          .height(24.dp)
                          .padding(end = 8.dp)
                          .testTag("ChefHatIcon"),
                  contentScale = ContentScale.Fit)

              // Title Text
              Text(
                  text = title,
                  style =
                      TextStyle(
                          fontSize = 20.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color(0xFF000000)),
                  modifier = Modifier.width(118.dp).height(24.dp).testTag("AppBarTitle"))
            }
      },
      navigationIcon = {
        // Back Arrow Icon
        IconButton(onClick = onBackClick, modifier = Modifier.testTag("BackButton")) {
          Icon(
              imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
              contentDescription = "Back",
              modifier = Modifier.width(30.dp).height(30.dp))
        }
      },
      colors = TopAppBarDefaults.topAppBarColors(containerColor = lightCream),
      modifier = Modifier.padding(end = 30.dp).testTag("TopAppBar"))
}
