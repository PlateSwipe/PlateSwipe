package com.android.sample.ui.createRecipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeNameScreen(
    modifier: Modifier = Modifier,
    currentStep: Int = 0,
    navigationActions: NavigationActions, // Added for navigation
) {
  var recipeName by remember { mutableStateOf(TextFieldValue("")) }
  var showError by remember { mutableStateOf(false) }

  Box(
      modifier = modifier.padding(10.dp), // Global padding
      contentAlignment = Alignment.TopCenter) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize()) {
              RecipeProgressBar(currentStep = currentStep)

              Spacer(modifier = Modifier.height(30.dp))

              // Title "Create your recipe"
              Text(
                  text = "Create your recipe",
                  style =
                      TextStyle(
                          fontSize = 32.sp,
                          lineHeight = 20.sp,
                          fontFamily = MeeraInimai,
                          fontWeight = FontWeight.Bold,
                          color = Color.Black,
                          letterSpacing = 0.32.sp,
                      ),
                  modifier =
                      Modifier.fillMaxWidth().padding(horizontal = 32.dp).testTag("RecipeTitle"),
                  textAlign = TextAlign.Center)

              Spacer(modifier = Modifier.height(10.dp))

              // Description text
              Text(
                  text =
                      "Create a recipe that others can discover and enjoy. Start by giving your dish a name!",
                  style =
                      TextStyle(
                          fontSize = 14.sp,
                          lineHeight = 20.sp,
                          fontFamily = MeeraInimai,
                          fontWeight = FontWeight.Normal,
                          color = Color.Black,
                          letterSpacing = 0.14.sp,
                      ),
                  modifier =
                      Modifier.padding(horizontal = 32.dp)
                          .width(260.dp)
                          .height(63.dp)
                          .testTag("RecipeSubtitle"),
                  textAlign = TextAlign.Center)

              Spacer(modifier = Modifier.height(30.dp))

              Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Name of the recipe",
                    style =
                        TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontFamily = Roboto,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            letterSpacing = 0.14.sp,
                        ),
                    modifier = Modifier.padding(horizontal = 16.dp))

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = recipeName,
                    onValueChange = {
                      recipeName = it
                      showError = false
                    },
                    label = {
                      Text(
                          text = "Choose a catchy title that reflects your dish",
                          style =
                              TextStyle(
                                  fontSize = 10.sp,
                                  lineHeight = 20.sp,
                                  fontFamily = MeeraInimai,
                                  fontWeight = FontWeight.Normal,
                                  color = C4,
                                  letterSpacing = 0.1.sp,
                              ))
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier =
                        Modifier.fillMaxWidth()
                            .height(60.dp)
                            .padding(horizontal = 16.dp)
                            .background(lightCream, shape = RoundedCornerShape(8.dp))
                            .testTag("recipeNameTextField"),
                    colors =
                        TextFieldDefaults.outlinedTextFieldColors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                        ),
                    textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal))
              }

              if (showError) {
                Text(
                    text = "Please enter a recipe name",
                    color = Color.Red,
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 8.dp))
              }

              // Chef image and "Next Step" button
              Box(
                  modifier = Modifier.fillMaxWidth().wrapContentHeight().offset(y = 60.dp),
                  contentAlignment = Alignment.BottomCenter) {
                    ChefImage(Modifier, (-80).dp, 40.dp)

                    Spacer(modifier = Modifier.height(20.dp))

                    // Button that navigates to the next step
                    Button(
                        onClick = {
                          if (recipeName.text.isEmpty()) {
                            showError = true
                          } else {
                            navigationActions.navigateTo(Screen.CREATE_RECIPE_INGREDIENTS)
                          }
                        },
                        modifier =
                            Modifier.width(261.dp)
                                .height(46.dp)
                                .background(
                                    color = lightCream, shape = RoundedCornerShape(size = 4.dp))
                                .align(Alignment.BottomCenter)
                                .testTag("NextStepButton")) {
                          Text("Next Step")
                        }
                  }
            }
      }
}
