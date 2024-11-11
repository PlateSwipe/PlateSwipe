package com.android.sample.ui.createRecipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.resources.C.Dimension.CreateRecipeListInstructionsScreen.BIG_PADDING
import com.android.sample.resources.C.Dimension.CreateRecipeListInstructionsScreen.CARD_BORDER_ROUND
import com.android.sample.resources.C.Dimension.CreateRecipeListInstructionsScreen.CARD_CORNER_RADIUS
import com.android.sample.resources.C.Dimension.CreateRecipeListInstructionsScreen.CARD_SHADOW_ELEVATION
import com.android.sample.resources.C.Dimension.CreateRecipeListInstructionsScreen.CURRENT_STEP
import com.android.sample.resources.C.Dimension.CreateRecipeListInstructionsScreen.ICON_SIZE
import com.android.sample.resources.C.Dimension.CreateRecipeListInstructionsScreen.MEDIUM_PADDING
import com.android.sample.resources.C.Dimension.CreateRecipeListInstructionsScreen.REALLY_SMALL_PADDING
import com.android.sample.resources.C.Dimension.CreateRecipeListInstructionsScreen.ROUNDED_CORNER_SHAPE
import com.android.sample.resources.C.Dimension.CreateRecipeListInstructionsScreen.ROW_SIZE
import com.android.sample.resources.C.Tag.RECIPE_NAME_BASE_PADDING
import com.android.sample.resources.C.Tag.RECIPE_NAME_BUTTON_HEIGHT
import com.android.sample.resources.C.Tag.RECIPE_NAME_BUTTON_WIDTH
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.EDIT_INSTRUCTION_ICON
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.INSTRUCTION_LIST
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.INSTRUCTION_LIST_ITEM
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.INSTRUCTION_TEXT
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.INSTRUCTION_TEXT_IN_CARD
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.INSTRUCTION_TEXT_SPACE
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.INSTRUCTION_TIME
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.NEXT_STEP_BUTTON
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.RECIPE_LIST_INSTRUCTIONS_SCREEN_SPACER1
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.RECIPE_LIST_INSTRUCTION_ICON
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.RECIPE_LIST_ITEM_THUMBNAIL
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.RECIPE_NAME_TEXT
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.SCREEN_COLUMN
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
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
      content = { paddingValues ->
        RecipeListInstructionsContent(
            createRecipeViewModel = createRecipeViewModel,
            modifier = Modifier.padding(paddingValues = paddingValues),
            navigationActions = navigationActions,
        )
      })
}

@Composable
fun RecipeListInstructionsContent(
    createRecipeViewModel: CreateRecipeViewModel,
    modifier: Modifier,
    navigationActions: NavigationActions,
) {

  Column(
      modifier = modifier.padding(RECIPE_NAME_BASE_PADDING).testTag(SCREEN_COLUMN),
  ) {
    RecipeProgressBar(currentStep = CURRENT_STEP)
    Spacer(
        modifier = Modifier.height(BIG_PADDING.dp).testTag(RECIPE_LIST_INSTRUCTIONS_SCREEN_SPACER1))
    Text(
        modifier = Modifier.testTag(RECIPE_NAME_TEXT),
        text = createRecipeViewModel.getRecipeName(),
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onPrimary,
    )
    Text(
        modifier = Modifier.testTag(INSTRUCTION_TEXT),
        text = stringResource(R.string.RecipeListInstructionsScreen_Instructions),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onPrimary,
    )
    // LazyColumn for the scrollable list of instructions
    LazyColumn(
        contentPadding = PaddingValues(vertical = SMALL_PADDING.dp),
        modifier =
            Modifier.weight(1f)
                .background(
                    brush =
                        Brush.verticalGradient(
                            colors =
                                listOf(MaterialTheme.colorScheme.background, Color.Transparent)))
                .align(Alignment.CenterHorizontally)
                // These parameters come from https://stackoverflow.com/questions/66762472/how-to-add-fading-edge-effect-to-android-jetpack-compose-column-or-row
                .fadingEdge(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.05f to Color.Red,
                        0.7f to Color.Red,
                        1f to Color.Transparent))
                .testTag(INSTRUCTION_LIST)) {

          /**
           * This Part will be later improved by adding the list of instructions
           *
           * items(createRecipeViewModel.getInstructions().length) { index ->
           * InstructionValue(index, recipeBuilder.getTime(), 0,
           * {navigationActions.navigateTo(Screen.CREATE_RECIPE_ADD_INSTRUCTION)}) }
           */
          //

          items(10) { index ->
            InstructionValue(
                index = index,
                time = createRecipeViewModel.getRecipeTime(),
                icon = 0,
                onClick = {
                  createRecipeViewModel.selectDescription(index = index)
                  navigationActions.navigateTo(Screen.CREATE_RECIPE_ADD_INSTRUCTION)
                })
          }

          /*
            item {
              InstructionValue(
                  index = 0,
                  time = createRecipeViewModel.getRecipeTime(),
                  icon = 0,
                  onClick = { index ->
                    createRecipeViewModel.selectDescription(index = index)
                    navigationActions.navigateTo(Screen.CREATE_RECIPE_ADD_INSTRUCTION)
                  })
            }
          */
        }

    // Fixed button at the bottom
    NextStepButton(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        navigationActions = navigationActions,
    )
  }
}

@Composable
fun NextStepButton(
    modifier: Modifier,
    navigationActions: NavigationActions,
) {
  Button(
      onClick = { navigationActions.navigateTo(Screen.PUBLISH_CREATED_RECIPE) },
      modifier =
          modifier
              .width(RECIPE_NAME_BUTTON_WIDTH)
              .height(RECIPE_NAME_BUTTON_HEIGHT)
              .background(
                  color = MaterialTheme.colorScheme.primary,
                  shape = RoundedCornerShape(size = ROUNDED_CORNER_SHAPE.dp))
              .testTag(NEXT_STEP_BUTTON),
      colors =
          ButtonDefaults.buttonColors(
              MaterialTheme.colorScheme.primary,
              contentColor = MaterialTheme.colorScheme.onPrimary),
      shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE.dp)) {
        Text(text = stringResource(R.string.next_step), textAlign = TextAlign.Center)
      }
}

@Composable
fun InstructionValue(index: Int, time: String?, icon: Int, onClick: (Int) -> Unit) {
  val officialStep = index + 1
  Card(
      modifier =
          Modifier.testTag(INSTRUCTION_LIST_ITEM)
              .fillMaxWidth(0.9f)
              .padding(horizontal = MEDIUM_PADDING.dp)
              .padding(vertical = REALLY_SMALL_PADDING.dp)
              .clickable(onClick = { onClick(index) })
              .shadow(
                  elevation = CARD_SHADOW_ELEVATION.dp,
                  shape = RoundedCornerShape(CARD_BORDER_ROUND.dp),
              ),
      colors =
          CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.background, // Background color of the card
              contentColor = MaterialTheme.colorScheme.onPrimary // Content color of the card
              ),
      shape = RoundedCornerShape(CARD_CORNER_RADIUS.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(SMALL_PADDING.dp)) {
          // Date and Status Row
          Row(
              modifier = Modifier.fillMaxWidth(ROW_SIZE).testTag(RECIPE_LIST_ITEM_THUMBNAIL),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically) {

              Image(
                  painter = painterResource(id = R.drawable.fire),
                  contentDescription = "Fire",
                  modifier = Modifier.size(ICON_SIZE.dp).testTag(RECIPE_LIST_INSTRUCTION_ICON))

                Column(modifier = Modifier.testTag(INSTRUCTION_TEXT_SPACE)) {
                  Text(
                      modifier = Modifier.testTag(INSTRUCTION_TEXT_IN_CARD),
                      text =
                          "${stringResource(R.string.RecipeListInstructionsScreen_Step)} $officialStep",
                      style = MaterialTheme.typography.bodyMedium,
                      fontWeight = FontWeight.Bold)

                  if (!time.isNullOrBlank()) {
                    Text(
                        text =
                            "$time ${stringResource(R.string.RecipeListInstructionsScreen_Minutes)}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.testTag(INSTRUCTION_TIME))
                  }
                }
                Icon(
                    imageVector = Icons.Default.ModeEdit,
                    contentDescription = "Edit",
                    modifier = Modifier.size(ICON_SIZE.dp).testTag(EDIT_INSTRUCTION_ICON))
              }
        }
      }
}

/**
 * This function enables to make a Fade with the content Source
 * https://stackoverflow.com/questions/66762472/how-to-add-fading-edge-effect-to-android-jetpack-compose-column-or-row
 *
 * @param brush The brush to use for the fading effect
 */
fun Modifier.fadingEdge(brush: Brush) =
    this.graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen).drawWithContent {
      drawContent()
      drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }
