package com.android.sample.ui.fridge

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import com.android.sample.R
import com.android.sample.model.fridge.FridgeItem
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.ingredient.IngredientViewModel
import com.android.sample.model.user.UserViewModel
import com.android.sample.resources.C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_IMAGE_BORDER_RADIUS
import com.android.sample.resources.C.Dimension.CreateRecipeListInstructionsScreen.CARD_BORDER_ROUND
import com.android.sample.resources.C.Dimension.FridgeScreen.ALL_BAR
import com.android.sample.resources.C.Dimension.FridgeScreen.BAR_HEIGHT
import com.android.sample.resources.C.Dimension.FridgeScreen.BAR_ROUND_CORNER
import com.android.sample.resources.C.Dimension.FridgeScreen.BORDER_FRIDGE_WIDTH
import com.android.sample.resources.C.Dimension.FridgeScreen.CARD_ELEVATION
import com.android.sample.resources.C.Dimension.FridgeScreen.EDIT_ICON_SIZE
import com.android.sample.resources.C.Dimension.FridgeScreen.EMPTY_FRIDGE_FONT_SIZE
import com.android.sample.resources.C.Dimension.FridgeScreen.FRIDGE_TAG_CORNER
import com.android.sample.resources.C.Dimension.FridgeScreen.INGREDIENT_IMAGE_SIZE
import com.android.sample.resources.C.Dimension.FridgeScreen.INGREDIENT_MAX_LINE
import com.android.sample.resources.C.Dimension.FridgeScreen.INGREDIENT_NAME_FONT_SIZE
import com.android.sample.resources.C.Dimension.FridgeScreen.ITEM_ALPHA
import com.android.sample.resources.C.Dimension.FridgeScreen.MAX_ORANGE_DAY
import com.android.sample.resources.C.Dimension.FridgeScreen.MAX_PROPORTION
import com.android.sample.resources.C.Dimension.FridgeScreen.MIN_ORANGE_DAY
import com.android.sample.resources.C.Dimension.FridgeScreen.MIN_PROPORTION
import com.android.sample.resources.C.Dimension.FridgeScreen.NUMBER_CARD_IN_A_ROW
import com.android.sample.resources.C.Dimension.FridgeScreen.TITLE_FONT_SIZE
import com.android.sample.resources.C.Dimension.PADDING_16
import com.android.sample.resources.C.Dimension.PADDING_32
import com.android.sample.resources.C.Dimension.PADDING_4
import com.android.sample.resources.C.Dimension.PADDING_8
import com.android.sample.resources.C.Tag.BASE_PADDING
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_THUMBNAIL_URL
import com.android.sample.resources.C.TestTag.Fridge.GREEN
import com.android.sample.resources.C.TestTag.Fridge.ORANGE
import com.android.sample.resources.C.TestTag.Fridge.RED
import com.android.sample.resources.C.TestTag.SwipePage.RECIPE_IMAGE_1
import com.android.sample.ui.createRecipe.ChefImage
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.firebrickRed
import com.android.sample.ui.theme.grayBorder
import com.android.sample.ui.theme.jungleGreen
import com.android.sample.ui.theme.orangeExpirationBar
import com.android.sample.ui.utils.ConfirmationPopUp
import com.android.sample.ui.utils.PlateSwipeButton
import com.android.sample.ui.utils.PlateSwipeScaffold
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.max

/**
 * Fridge Screen
 *
 * @param navigationActions: NavigationActions object
 * @param userViewModel: UserViewModel object
 * @return Unit
 *
 * Function to display the Fridge Screen
 *
 * @see FridgeScreen: Composable function to display the Fridge Screen
 * @see NavigationActions: Class to handle navigation actions
 * @see IngredientViewModel: Class to handle ingredient view model
 */
@Composable
fun FridgeScreen(navigationActions: NavigationActions, userViewModel: UserViewModel) {

  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = navigationActions.currentRoute(),
      content = { paddingValues ->
        val listFridgeItem by userViewModel.fridgeItems.collectAsState()
        if (listFridgeItem.isEmpty()) {
          EmptyFridge(paddingValues, navigationActions, userViewModel)
        } else {
          FridgeContent(navigationActions, paddingValues, userViewModel, listFridgeItem)
        }
      },
      showBackArrow = true)
}

/**
 * Empty Fridge
 *
 * @param paddingValues: padding values to be applied to the composable
 * @param navigationActions: navigation actions to be applied to the composable
 * @param userViewModel: user view model to be applied to the composable
 */
@Composable
private fun EmptyFridge(
    paddingValues: PaddingValues,
    navigationActions: NavigationActions,
    userViewModel: UserViewModel
) {
  Box(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(BASE_PADDING)) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize()) {
          // Title text
          Text(
              text = stringResource(R.string.empty_fridge_title),
              style =
                  MaterialTheme.typography.titleMedium.copy(lineHeight = EMPTY_FRIDGE_FONT_SIZE.sp),
              fontSize = EMPTY_FRIDGE_FONT_SIZE.sp,
              color = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.fillMaxWidth().padding(PADDING_32.dp),
              textAlign = TextAlign.Center)
          Spacer(modifier = Modifier.size(PADDING_16.dp))
          // Subtitle text
          Text(
              text = stringResource(R.string.empty_fridge_description),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.padding(horizontal = PADDING_32.dp).zIndex(1f),
              textAlign = TextAlign.Center)

          Spacer(modifier = Modifier.weight(2f))

          // Row to hold the chef image and change its position horizontally
          Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.weight(1f))
            ChefImage(modifier = Modifier.weight(18f).zIndex(-1f))
            Spacer(modifier = Modifier.weight(1f))
          }
          Spacer(modifier = Modifier.weight(2f))
        }

    // Action button
    PlateSwipeButton(
        stringResource(R.string.add_ingredient),
        modifier = Modifier.align(Alignment.BottomCenter),
        onClick = {
          userViewModel.clearIngredientList()
          userViewModel.clearSearchingIngredientList()
          userViewModel.clearIngredient()
          navigationActions.navigateTo(Screen.FRIDGE_SEARCH_ITEM)
        })
  }
}

/**
 * Fridge Content when not empty
 *
 * @param navigationActions: NavigationActions object to handle navigation actions
 * @param paddingValues: PaddingValues object to apply padding to the composable
 * @param userViewModel: UserViewModel object to handle user view model
 * @param listFridgeItem: List<Pair<FridgeItem, Ingredient>> object to hold the list of fridge items
 */
@Composable
private fun FridgeContent(
    navigationActions: NavigationActions,
    paddingValues: PaddingValues,
    userViewModel: UserViewModel,
    listFridgeItem: List<Pair<FridgeItem, Ingredient>>
) {
  Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
    Column(
        modifier = Modifier.fillMaxSize().weight(1f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
          // Row to display the title and number of items
          Row(
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(
                          start = PADDING_32.dp,
                          end = PADDING_32.dp,
                          top = PADDING_16.dp,
                          bottom = PADDING_8.dp),
              horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = stringResource(R.string.fridge_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = TITLE_FONT_SIZE.sp,
                    color = MaterialTheme.colorScheme.onPrimary)
                Text(
                    text = "${listFridgeItem.size} items",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = TITLE_FONT_SIZE.sp,
                    color = MaterialTheme.colorScheme.onPrimary)
              }

          // Card that represents the fridge
          Card(
              modifier =
                  Modifier.fillMaxSize()
                      .padding(PADDING_16.dp)
                      .border(
                          width = BORDER_FRIDGE_WIDTH.dp,
                          color = grayBorder,
                          shape = RoundedCornerShape(CARD_BORDER_ROUND.dp)),
              shape = RoundedCornerShape(CARD_BORDER_ROUND.dp),
              elevation = CardDefaults.cardElevation(defaultElevation = CARD_ELEVATION.dp),
              colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary)) {
                // Lazy Column to display the fridge items
                LazyColumn(modifier = Modifier.fillMaxSize().background(Color.Transparent)) {
                  item {
                    listFridgeItem
                        .sortedBy { it.first.expirationDate }
                        .chunked(NUMBER_CARD_IN_A_ROW)
                        .forEach { chunk ->
                          Row(
                              modifier = Modifier.fillMaxWidth(),
                              horizontalArrangement = Arrangement.Center // Distribute cards evenly
                              ) {
                                // First card in the chunk
                                chunk.getOrNull(0)?.let { card1 ->
                                  ItemCard(
                                      Modifier.weight(1f), card1, userViewModel, navigationActions)
                                }

                                // Second card in the chunk (if present)
                                chunk.getOrNull(1)?.let { card2 ->
                                  ItemCard(
                                      Modifier.weight(1f), card2, userViewModel, navigationActions)
                                }

                                // Add an empty spacer if only one card exists in the chunk
                                if (chunk.size == 1) {
                                  Spacer(modifier = Modifier.weight(1f).padding(PADDING_16.dp))
                                }
                              }
                        }
                  }
                }
              }
        }
    // button to add ingredient
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.Transparent),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
          PlateSwipeButton(
              stringResource(R.string.add_ingredient),
              modifier = Modifier.padding(PADDING_16.dp),
              onClick = {
                userViewModel.clearIngredientList()
                userViewModel.clearSearchingIngredientList()
                userViewModel.clearIngredient()
                navigationActions.navigateTo(Screen.FRIDGE_SEARCH_ITEM)
              })
        }
  }
}

/**
 * Item Card
 *
 * @param modifier: Modifier object to apply to the composable
 * @param card: Pair<FridgeItem, Ingredient> object to hold the fridge item and ingredient
 * @param userViewModel: UserViewModel object to handle user view model
 */
@SuppressLint("AutoboxingStateCreation")
@Composable
private fun ItemCard(
    modifier: Modifier,
    card: Pair<FridgeItem, Ingredient>,
    userViewModel: UserViewModel,
    navigationActions: NavigationActions
) {

  val confirmationRemoveDisplay = remember { mutableStateOf(false) }
  Column(
      modifier = modifier.padding(PADDING_8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
        Card(
            modifier = Modifier.padding(PADDING_8.dp),
            colors = CardDefaults.cardColors(Color.White),
            shape = RoundedCornerShape(CARD_BORDER_ROUND.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = CARD_ELEVATION.dp)) {
              Box(modifier = Modifier.fillMaxSize()) {
                // Pencil Icon - Positioned at Top Right
                IconButton(
                    onClick = {
                      userViewModel.setEditingIngredient(card)
                      userViewModel.clearIngredientList()
                      userViewModel.addIngredient(card.second)
                      navigationActions.navigateTo(Screen.FRIDGE_EDIT)
                    },
                    modifier =
                        Modifier.align(Alignment.TopEnd)
                            .padding(PADDING_8.dp)
                            .size(EDIT_ICON_SIZE.dp),
                ) {
                  Icon(
                      imageVector = Icons.Default.Edit,
                      contentDescription = "Edit ${card.second.name} Quantity",
                      tint = MaterialTheme.colorScheme.onSecondary,
                  )
                }

                IconButton(
                    onClick = { confirmationRemoveDisplay.value = true },
                    modifier =
                        Modifier.align(Alignment.TopStart)
                            .padding(PADDING_8.dp)
                            .size(EDIT_ICON_SIZE.dp)) {
                      Icon(
                          imageVector = Icons.Default.RemoveCircleOutline,
                          contentDescription = "Remove ${card.second.name} from fridge",
                          tint = MaterialTheme.colorScheme.error,
                      )
                    }

                // Column to display the image, quantity, and expiration bar
                Column(
                    modifier = Modifier.fillMaxSize().padding(vertical = PADDING_8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                      Spacer(Modifier.size(EDIT_ICON_SIZE.dp).padding(PADDING_8.dp))

                      Image(
                          painter =
                              rememberAsyncImagePainter(
                                  model = card.second.images[PRODUCT_FRONT_IMAGE_THUMBNAIL_URL]),
                          contentDescription = stringResource(R.string.recipe_image),
                          modifier =
                              Modifier.size(INGREDIENT_IMAGE_SIZE.dp)
                                  .padding(PADDING_8.dp)
                                  .testTag(RECIPE_IMAGE_1)
                                  .clip(
                                      RoundedCornerShape(
                                          INGREDIENT_DISPLAY_IMAGE_BORDER_RADIUS.dp)),
                          contentScale = ContentScale.Fit)

                      // Row to display the quantity tag
                      Row(
                          modifier =
                              Modifier.padding(
                                      PADDING_8.dp, PADDING_8.dp, PADDING_8.dp, PADDING_8.dp)
                                  .background(
                                      color = MaterialTheme.colorScheme.onBackground,
                                      shape = RoundedCornerShape(FRIDGE_TAG_CORNER.dp)),
                          verticalAlignment = Alignment.CenterVertically,
                          horizontalArrangement = Arrangement.Center) {
                            Text(
                                modifier =
                                    Modifier.padding(
                                        horizontal = PADDING_16.dp, vertical = PADDING_4.dp),
                                text = "${card.first.quantity} x ${card.second.quantity}",
                                fontSize = 14.sp,
                                color = Color.White)
                          }

                      ExpirationBar(
                          testTag = "expirationBar${card.second.name}${card.first.expirationDate}",
                          expirationDate = card.first.expirationDate)
                    }
              }
            }
        Text(
            modifier =
                Modifier.padding(vertical = (PADDING_4 / 2).dp, horizontal = PADDING_8.dp)
                    .align(Alignment.CenterHorizontally),
            text = card.second.name,
            style = MaterialTheme.typography.titleMedium,
            fontSize = INGREDIENT_NAME_FONT_SIZE.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = INGREDIENT_MAX_LINE,
            overflow = TextOverflow.Ellipsis)
      }

  if (confirmationRemoveDisplay.value) {
    ConfirmationPopUp(
        onConfirm = {
          userViewModel.removeIngredientFromUserFridge(card.second, card.first.expirationDate)
          confirmationRemoveDisplay.value = false
        },
        onDismiss = { confirmationRemoveDisplay.value = false },
        titleText = "Delete ${card.second.name}",
        confirmationText = "Are you sure you want to remove ${card.second.name} from your fridge?",
    )
  }
}

/**
 * Expiration Bar of an ingredient
 *
 * @param expirationDate: LocalDate object to hold the expiration date
 * @param testTag: String object to hold the test tag
 */
@Composable
private fun ExpirationBar(expirationDate: LocalDate, testTag: String) {
  val today = LocalDate.now()
  val daysLeft = expirationDate.let { ChronoUnit.DAYS.between(today, it).toInt() }

  // Define bar properties based on the days left
  val (barColor, tagColor) =
      when {
        daysLeft > MAX_ORANGE_DAY -> jungleGreen to GREEN
        daysLeft in MIN_ORANGE_DAY..MAX_ORANGE_DAY -> orangeExpirationBar to ORANGE
        else -> firebrickRed to RED
      }
  // Calculate the width of the bar based on the days left
  val widthFraction =
      when {
        daysLeft >= (MAX_PROPORTION - MIN_PROPORTION * MAX_PROPORTION) -> MIN_PROPORTION
        daysLeft <= 0 -> ALL_BAR
        else -> (MAX_PROPORTION - daysLeft.toFloat()) / MAX_PROPORTION
      }
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Row(
        modifier =
            Modifier.testTag("$testTag$tagColor")
                .fillMaxWidth()
                .padding(horizontal = PADDING_16.dp, vertical = PADDING_8.dp),
        horizontalArrangement = Arrangement.Start) {
          // Background Bar
          Box(
              modifier =
                  Modifier.fillMaxWidth()
                      .height(BAR_HEIGHT.dp)
                      .clip(RoundedCornerShape(BAR_ROUND_CORNER.dp))
                      .background(Color.LightGray)) {
                // Expiration Bar
                Box(
                    modifier =
                        Modifier.fillMaxWidth(fraction = widthFraction)
                            .height(BAR_HEIGHT.dp)
                            .clip(RoundedCornerShape(BAR_ROUND_CORNER.dp))
                            .background(barColor)
                            .padding(horizontal = PADDING_4.dp)
                            .zIndex(1f))
              }
        }
    if (daysLeft > 0) {
      Text(
          text = "${max(daysLeft, 0)} ${if (daysLeft == 1) "day" else "days"} left",
          modifier = Modifier.padding(PADDING_8.dp),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onPrimary.copy(alpha = ITEM_ALPHA))
    } else {
      Row(
          modifier = Modifier.fillMaxSize(),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Block,
                contentDescription = stringResource(R.string.expired),
                tint = MaterialTheme.colorScheme.error,
            )

            Text(
                text = stringResource(R.string.expired),
                modifier = Modifier.padding(PADDING_8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error)
          }
    }
  }
}
