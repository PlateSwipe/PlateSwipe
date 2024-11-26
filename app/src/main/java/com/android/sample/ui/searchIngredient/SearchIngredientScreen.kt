package com.android.sample.ui.searchIngredient

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.animation.LoadingCook
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.ingredient.SearchIngredientViewModel
import com.android.sample.resources.C.Dimension.IngredientSearchScreen.ICON_SCANNER_SIZE
import com.android.sample.resources.C.Dimension.IngredientSearchScreen.ICON_SCANNER_WEIGHT
import com.android.sample.resources.C.Dimension.IngredientSearchScreen.IMAGE_WEIGHT
import com.android.sample.resources.C.Dimension.IngredientSearchScreen.INGREDIENT_ITEM_CORNER
import com.android.sample.resources.C.Dimension.IngredientSearchScreen.INGREDIENT_ITEM_ELEVATION
import com.android.sample.resources.C.Dimension.IngredientSearchScreen.INGREDIENT_ITEM_MAX_LINE
import com.android.sample.resources.C.Dimension.IngredientSearchScreen.LOADING_COOK_SIZE
import com.android.sample.resources.C.Dimension.IngredientSearchScreen.LOADING_COOK_WEIGHT
import com.android.sample.resources.C.Dimension.IngredientSearchScreen.POP_UP_CLIP
import com.android.sample.resources.C.Dimension.IngredientSearchScreen.POP_UP_ELEVATION
import com.android.sample.resources.C.Dimension.IngredientSearchScreen.RESULT_FONT_SIZE
import com.android.sample.resources.C.Dimension.IngredientSearchScreen.SPACER_WEIGHT
import com.android.sample.resources.C.Dimension.PADDING_16
import com.android.sample.resources.C.Tag.IngredientSearchScreen.DO_NOT_SHOW_CONFIRMATION
import com.android.sample.resources.C.Tag.IngredientSearchScreen.INITIAL_LOADING_STATE
import com.android.sample.resources.C.TestTag.IngredientSearchScreen.CANCEL_BUTTON
import com.android.sample.resources.C.TestTag.IngredientSearchScreen.CONFIRMATION_BUTTON
import com.android.sample.resources.C.TestTag.IngredientSearchScreen.CONFIRMATION_POPUP
import com.android.sample.resources.C.TestTag.IngredientSearchScreen.DRAGGABLE_ITEM
import com.android.sample.resources.C.TestTag.IngredientSearchScreen.SCANNER_ICON
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.utils.PlateSwipeScaffold
import com.android.sample.ui.utils.SearchBar

/**
 * A composable that displays the ingredient search screen.
 *
 * @param navigationActions the navigation actions.
 * @param searchIngredientViewModel the view model for the ingredient.
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun SearchIngredientScreen(
    navigationActions: NavigationActions,
    searchIngredientViewModel: SearchIngredientViewModel,
    popUpTitle: String,
    popUpConfirmationText: String,
    popUpConfirmationButtonText: String,
    onConfirmation: (Ingredient) -> Unit,
) {
  val listIngredient = searchIngredientViewModel.searchingIngredientList.collectAsState()
  var showConfirmation by remember { mutableStateOf(DO_NOT_SHOW_CONFIRMATION) }
  var selectedIngredient by remember { mutableStateOf<Ingredient?>(null) }
  var isLoading by remember { mutableStateOf(INITIAL_LOADING_STATE) }

  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = navigationActions.currentRoute(),
      showBackArrow = true,
      content = { paddingValues ->
        LaunchedEffect(listIngredient.value) { isLoading = false }

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.testTag(DRAGGABLE_ITEM).fillMaxSize().padding(paddingValues)) {
              SearchDisplay(
                  searchIngredientViewModel = searchIngredientViewModel,
                  navigationActions = navigationActions,
                  onValueChange = { query -> isLoading = query.isNotEmpty() })

              ResultDisplay()

              // Display the list of ingredients
              Column(
                  modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                  verticalArrangement = Arrangement.Top,
                  horizontalAlignment = Alignment.CenterHorizontally) {
                    if (isLoading) {
                      LoadingCook(
                          modifier = Modifier.weight(LOADING_COOK_WEIGHT), size = LOADING_COOK_SIZE)
                      Spacer(modifier = Modifier.weight(SPACER_WEIGHT))
                    } else {
                      for (ingredient in listIngredient.value) {
                        IngredientItem(
                            ingredient = ingredient.first,
                            onClick = {
                              selectedIngredient = ingredient.first
                              showConfirmation = true
                            })
                      }
                    }
                  }
              // Display the confirmation pop-up if the user selects an ingredient
              if (showConfirmation && selectedIngredient != null) {
                ConfirmationPopUp(
                    onConfirm = {
                      onConfirmation(selectedIngredient!!)
                      selectedIngredient = null
                      showConfirmation = false
                    },
                    onDismiss = {
                      selectedIngredient = null
                      showConfirmation = false
                    },
                    titleText = popUpTitle,
                    confirmationText = popUpConfirmationText,
                    confirmationButtonText = popUpConfirmationButtonText)
              }
            }
      })
}

/**
 * A composable that displays the search bar and scanner icon.
 *
 * @param searchIngredientViewModel the view model for the ingredient.
 * @param navigationActions the navigation actions.
 * @param onValueChange the callback to invoke when the value changes.
 */
@Composable
private fun SearchDisplay(
    searchIngredientViewModel: SearchIngredientViewModel,
    navigationActions: NavigationActions,
    onValueChange: (String) -> Unit
) {
  Row(
      modifier = Modifier.fillMaxWidth().padding(PADDING_16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center) {
        // Display the search bar and scanner icon
        Spacer(modifier = Modifier.width(PADDING_16.dp).weight(SPACER_WEIGHT))
        SearchBar(
            modifier = Modifier.padding(PADDING_16.dp).weight(IMAGE_WEIGHT),
            onValueChange = onValueChange,
            onDebounce = { query ->
              if (query.isNotEmpty()) {
                searchIngredientViewModel.fetchIngredientByName(query)
              }
            })
        Icon(
            painter = painterResource(id = R.drawable.scanner),
            modifier =
                Modifier.weight(ICON_SCANNER_WEIGHT)
                    .size(ICON_SCANNER_SIZE.dp)
                    .testTag(SCANNER_ICON)
                    .clickable { navigationActions.navigateTo(Screen.CAMERA_SCAN_CODE_BAR) },
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = stringResource(R.string.scanner_instruction))
      }
}

/** A composable that displays the result text. */
@Composable
private fun ResultDisplay() {
  Row(
      modifier = Modifier.fillMaxWidth().padding(PADDING_16.dp),
      horizontalArrangement = Arrangement.Start,
  ) {
    Text(
        text = stringResource(R.string.result),
        style = MaterialTheme.typography.titleMedium.copy(fontSize = RESULT_FONT_SIZE.sp),
        color = MaterialTheme.colorScheme.onPrimary)
  }
}

/**
 * A composable that displays a confirmation pop-up.
 *
 * @param onConfirm the callback to invoke when the user confirms the action.
 * @param onDismiss the callback to invoke when the user dismisses the pop-up.
 */
@Composable
private fun ConfirmationPopUp(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    titleText: String,
    confirmationText: String,
    confirmationButtonText: String
) {
  AlertDialog(
      onDismissRequest = onDismiss,
      modifier =
          Modifier.fillMaxWidth()
              .padding(PADDING_16.dp)
              .shadow(
                  elevation = POP_UP_ELEVATION.dp, // Adjust elevation as desired
                  clip = POP_UP_CLIP // Ensures background respects the shadow's rounded corners
                  )
              .testTag(CONFIRMATION_POPUP),
      title = {
        Text(
            text = titleText,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary)
      },
      text = {
        Text(
            text = confirmationText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimary)
      },
      confirmButton = {
        TextButton(onClick = onConfirm, modifier = Modifier.testTag(CONFIRMATION_BUTTON)) {
          Text(
              text = confirmationButtonText,
              style = MaterialTheme.typography.titleSmall,
              color = MaterialTheme.colorScheme.onPrimary)
        }
      },
      dismissButton = {
        TextButton(onClick = onDismiss, modifier = Modifier.testTag(CANCEL_BUTTON)) {
          Text(
              text = stringResource(R.string.pop_up_cancel),
              style = MaterialTheme.typography.titleSmall,
              color = MaterialTheme.colorScheme.onPrimary)
        }
      },
      containerColor = MaterialTheme.colorScheme.secondary)
}

/**
 * A composable that displays an ingredient item.
 *
 * @param ingredient the ingredient to display.
 * @param onClick the callback to invoke when the ingredient item is clicked.
 */
@Composable
private fun IngredientItem(ingredient: Ingredient, onClick: () -> Unit) {
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .padding(PADDING_16.dp)
              .shadow(
                  elevation = INGREDIENT_ITEM_ELEVATION.dp, // Adjust elevation as desired
                  shape = RoundedCornerShape(INGREDIENT_ITEM_CORNER.dp),
                  clip = true // Ensures background respects the shadow's rounded corners
                  )
              .background(
                  MaterialTheme.colorScheme.secondary,
                  shape = RoundedCornerShape(INGREDIENT_ITEM_CORNER.dp))
              .testTag("ingredientItem${ingredient.name}")
              .clickable { onClick() }) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(PADDING_16.dp)) {
              Text(
                  text = ingredient.name,
                  style = MaterialTheme.typography.titleSmall,
                  color = MaterialTheme.colorScheme.onPrimary,
                  maxLines = INGREDIENT_ITEM_MAX_LINE,
                  overflow = TextOverflow.Ellipsis)
              ingredient.quantity?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary)
              }
            }
      }
}
