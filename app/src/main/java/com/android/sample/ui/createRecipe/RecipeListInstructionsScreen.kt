package com.android.sample.ui.createRecipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.resources.C
import com.android.sample.resources.C.Dimension.CameraScanCodeBarScreen.BOTTOM_BAR_HEIGHT
import com.android.sample.resources.C.Dimension.CameraScanCodeBarScreen.TOP_BAR_HEIGHT
import com.android.sample.resources.C.Tag.RECIPE_NAME_BASE_PADDING
import com.android.sample.resources.C.Tag.RECIPE_NAME_BUTTON_HEIGHT
import com.android.sample.resources.C.Tag.RECIPE_NAME_BUTTON_WIDTH
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.lightCream
import com.android.sample.ui.utils.PlateSwipeScaffold

@Composable
fun RecipeListInstructionsScreen(
    createRecipeViewModel: CreateRecipeViewModel,
    navigationActions: NavigationActions
) {

  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = Route.CREATE_RECIPE,
      showBackArrow = true,
      content = {
        RecipeListInstructionsContent(

            createRecipeViewModel = createRecipeViewModel,
            modifier = Modifier,
            navigationActions = navigationActions)
      })
}

@Composable
fun RecipeListInstructionsContent(
    createRecipeViewModel: CreateRecipeViewModel,
    modifier: Modifier,
    navigationActions: NavigationActions
) {

  Column(
      modifier =
          modifier
              .padding(top = TOP_BAR_HEIGHT.dp, bottom = BOTTOM_BAR_HEIGHT.dp)
              .fillMaxSize()
              .padding(RECIPE_NAME_BASE_PADDING)) {
        RecipeProgressBar(currentStep = 2)
        Spacer(
            modifier =
                Modifier.height(C.Dimension.CreateRecipeListInstructionsScreen.BIG_PADDING.dp)
                    .testTag("RecipeListInstructionsScreenSpacer1"))
        Text(
            modifier = Modifier.testTag("RecipeNameText"),
            text = createRecipeViewModel.getRecipeName(),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
        )
        Text(
            modifier = Modifier.testTag("InstructionsText"),
            text = stringResource(R.string.RecipeListInstructionsScreen_Instructions),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
        )
        // LazyColumn for the scrollable list of instructions
        LazyColumn(
            contentPadding =
                PaddingValues(
                    vertical = C.Dimension.CreateRecipeListInstructionsScreen.SMALL_PADDING.dp),
            modifier =
                modifier
                    .fillMaxWidth()
                    .fillMaxHeight(
                        C.Dimension.CreateRecipeListInstructionsScreen.LIST_HEIGHT_FRACTION)
                    .padding(
                        horizontal =
                            C.Dimension.CreateRecipeListInstructionsScreen.MEDIUM_PADDING.dp)
                    .padding(
                        top = C.Dimension.CreateRecipeListInstructionsScreen.SMALL_PADDING.dp,
                        bottom = C.Dimension.CreateRecipeListInstructionsScreen.SMALL_PADDING.dp)
                    .testTag("InstructionList")) {

              /**
               * This Part will be later improved by adding the list of instructions
               *
               * items(createRecipeViewModel.getInstructions().length) { index ->
               * InstructionValue(index, recipeBuilder.getTime(), 0,
               * {navigationActions.navigateTo(Screen.CREATE_RECIPE_ADD_INSTRUCTION)}) }
               */
              //

              item {
                InstructionValue(
                    0,
                    createRecipeViewModel.getRecipeTime(),
                    0,
                    { navigationActions.navigateTo(Screen.CREATE_RECIPE_ADD_INSTRUCTION) })
              }
            }
        Spacer(modifier = Modifier.weight(1f).testTag("RecipeListInstructionsScreenSpacer2"))
        // Fixed button at the bottom
        NextStepButton(
            modifier = modifier.align(Alignment.CenterHorizontally),
            navigationActions = navigationActions)
      }
}

@Composable
fun NextStepButton(modifier: Modifier, navigationActions: NavigationActions) {
  Button(
      onClick = { navigationActions.navigateTo(Screen.PUBLISH_CREATED_RECIPE) },
      modifier =
          modifier
              .width(RECIPE_NAME_BUTTON_WIDTH)
              .height(RECIPE_NAME_BUTTON_HEIGHT)
              .background(
                  color = lightCream,
                  shape =
                      RoundedCornerShape(
                          size =
                              C.Dimension.CreateRecipeListInstructionsScreen.ROUNDED_CORNER_SHAPE
                                  .dp))
              .testTag("NextStepButton"),
      colors = ButtonDefaults.buttonColors(lightCream, contentColor = Color.Black),
      shape =
          RoundedCornerShape(
              C.Dimension.CreateRecipeListInstructionsScreen.ROUNDED_CORNER_SHAPE.dp)) {
        Text(stringResource(R.string.next_step))
      }
}

@Composable
fun InstructionValue(index: Int, time: String?, icon: Int, onClick: () -> Unit) {
  val officialStep = index + 1
  Card(
      modifier =
          Modifier.testTag("InstructionListItem")
              .fillMaxWidth()
              .padding(vertical = 4.dp)
              .clickable(onClick = onClick)
              .border(
                  C.Dimension.CreateRecipeListInstructionsScreen.CARD_BORDER_THICKNESS.dp,
                  Color.Gray,
                  RoundedCornerShape(
                      C.Dimension.CreateRecipeListInstructionsScreen.CARD_BORDER_THICKNESS.dp)),
      colors =
          CardDefaults.cardColors(
              containerColor = Color.White, // Background color of the card
              contentColor = Color.Black // Content color of the card
              ),
  ) {
    Column(
        modifier =
            Modifier.fillMaxWidth()
                .padding(C.Dimension.CreateRecipeListInstructionsScreen.SMALL_PADDING.dp)) {
          // Date and Status Row
          Row(
              modifier = Modifier.fillMaxWidth(1f).testTag("InstructionThumbnail"),
              horizontalArrangement = Arrangement.SpaceBetween) {
                Image(
                    painter = painterResource(id = R.drawable.fire),
                    contentDescription = "Fire",
                    modifier =
                        Modifier.size(C.Dimension.CreateRecipeListInstructionsScreen.ICON_SIZE.dp)
                            .testTag("InstructionIcon"))
                Column(modifier = Modifier.testTag("InstructionTextSpace")) {
                  Text(
                      modifier = Modifier.testTag("InstructionText"),
                      text = "Step $officialStep",
                      style = MaterialTheme.typography.bodyMedium,
                      fontWeight = FontWeight.Bold)

                  if (!time.isNullOrBlank()) {
                    Text(
                        text = "$time min",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.testTag("InstructionTime"))
                  }
                }
                Icon(
                    imageVector = Icons.Default.ModeEdit,
                    contentDescription = "Edit",
                    modifier =
                        Modifier.size(C.Dimension.CreateRecipeListInstructionsScreen.ICON_SIZE.dp)
                            .testTag("EditInstructionIcon"))
              }
        }
  }
}
