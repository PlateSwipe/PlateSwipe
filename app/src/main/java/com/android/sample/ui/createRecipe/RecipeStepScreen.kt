package com.android.sample.ui.createRecipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.MeeraInimai
import com.android.sample.ui.theme.lightCream
import com.android.sample.ui.topbar.MyAppBar

@Composable
fun RecipeStepScreen(
    title: String,
    subtitle: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    navigationActions: NavigationActions,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
  Scaffold(
      topBar = {
        MyAppBar(
            onBackClick = { navigationActions.goBack() } // Handle back navigation using goBack()
            )
      }) { paddingValues ->
        Box(
            modifier = modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            contentAlignment = Alignment.TopCenter) {
              Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Top,
                  modifier = Modifier.fillMaxSize()) {
                    // Progress bar to show the current step
                    RecipeProgressBar(currentStep = currentStep)

                    Spacer(modifier = Modifier.height(50.dp))

                    // Title passed as a parameter
                    Text(
                        text = title,
                        style =
                            TextStyle(
                                fontSize = 32.sp,
                                lineHeight = 40.sp,
                                fontFamily = MeeraInimai,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                letterSpacing = 0.32.sp,
                            ),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                        textAlign = TextAlign.Center)

                    Spacer(modifier = Modifier.height(10.dp))

                    // Subtitle passed as a parameter
                    Text(
                        text = subtitle,
                        style =
                            TextStyle(
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                fontFamily = MeeraInimai,
                                fontWeight = FontWeight.Normal,
                                color = Color.Black,
                                letterSpacing = 0.14.sp,
                            ),
                        modifier = Modifier.padding(horizontal = 32.dp).width(260.dp).height(63.dp),
                        textAlign = TextAlign.Center)

                    Spacer(modifier = Modifier.height(50.dp))

                    // Button with dynamic text and action
                    Button(
                        onClick = onButtonClick,
                        modifier =
                            Modifier.width(200.dp)
                                .height(50.dp)
                                .background(
                                    color = lightCream, shape = RoundedCornerShape(size = 4.dp))
                                .align(Alignment.CenterHorizontally)) {
                          Text(buttonText)
                        }

                    // Chef image at the bottom of the screen
                    ChefImage(Modifier, (-80).dp, 115.dp)
                  }
            }
      }
}
