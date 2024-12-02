package com.android.sample.ui.fridge

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import com.android.sample.R
import com.android.sample.model.fridge.FridgeItem
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.ingredient.IngredientViewModel
import com.android.sample.model.user.UserViewModel
import com.android.sample.resources.C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_IMAGE_BORDER_RADIUS
import com.android.sample.resources.C.Dimension.CreateRecipeListInstructionsScreen.CARD_BORDER_ROUND
import com.android.sample.resources.C.Dimension.FridgeScreen.TITLE_FONT_SIZE
import com.android.sample.resources.C.Dimension.PADDING_16
import com.android.sample.resources.C.Dimension.PADDING_32
import com.android.sample.resources.C.Dimension.PADDING_8
import com.android.sample.resources.C.Tag.BASE_PADDING
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_THUMBNAIL_URL
import com.android.sample.resources.C.TestTag.SwipePage.RECIPE_IMAGE_1
import com.android.sample.ui.createRecipe.ChefImage
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.firebrickRed
import com.android.sample.ui.theme.tagBackground
import com.android.sample.ui.utils.PlateSwipeButton
import com.android.sample.ui.utils.PlateSwipeScaffold
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.max

/**
 * Fridge Screen
 *
 * @param navigationActions
 * @return Unit
 *
 * Function to display the Fridge Screen
 *
 * @see FridgeScreen
 * @see NavigationActions
 * @see IngredientViewModel
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

@Composable
fun EmptyFridge(
    paddingValues: PaddingValues,
    navigationActions: NavigationActions,
    userViewModel: UserViewModel
) {
  Box(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(BASE_PADDING)) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize()) {
          // Progress bar to show the current step

          // Title text
          Text(
              text = "Empty Fridge",
              style = MaterialTheme.typography.titleMedium.copy(lineHeight = 40.sp),
              fontSize = 40.sp,
              color = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.fillMaxWidth().padding(PADDING_32.dp),
              textAlign = TextAlign.Center)
          Spacer(modifier = Modifier.size(PADDING_16.dp))
          // Subtitle text
          Text(
              text = "Your fridge is currently empty, click below to add ingredient",
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
        "Add Ingredient",
        modifier = Modifier.align(Alignment.BottomCenter).testTag("NextStepButton"),
        onClick = {
          userViewModel.clearIngredientList()
          userViewModel.clearSearchingIngredientList()
          userViewModel.clearIngredient()
          navigationActions.navigateTo(Screen.FRIDGE_SEARCH_ITEM)
        })
  }
}

@Composable
fun FridgeContent(
    navigationActions: NavigationActions,
    paddingValues: PaddingValues,
    userViewModel: UserViewModel,
    listFridgeItem: List<Pair<FridgeItem, Ingredient>>
) {
  Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
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

          LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
              listFridgeItem
                  .sortedBy { it.first.expirationDate }
                  .chunked(2)
                  .forEach { chunk ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center // Distribute cards evenly
                        ) {
                          val cardWidth = 150.dp // Define a fixed width for the cards

                          // First card in the chunk
                          chunk.getOrNull(0)?.let { card1 ->
                            ItemCard(cardWidth, card1, userViewModel)
                          }

                          // Second card in the chunk (if present)
                          chunk.getOrNull(1)?.let { card2 ->
                            ItemCard(cardWidth, card2, userViewModel)
                          }

                          // Add an empty spacer if only one card exists in the chunk
                          if (chunk.size == 1) {
                            Spacer(modifier = Modifier.width(cardWidth).padding(4.dp))
                          }
                        }
                  }
            }
          }
        }
    // Action button
    PlateSwipeButton(
        "Add Ingredient",
        modifier =
            Modifier.padding(PADDING_16.dp).align(Alignment.BottomCenter).testTag("NextStepButton"),
        onClick = {
          userViewModel.clearIngredientList()
          userViewModel.clearSearchingIngredientList()
          userViewModel.clearIngredient()
          navigationActions.navigateTo(Screen.FRIDGE_SEARCH_ITEM)
        })
  }
}

@Composable
private fun ItemCard(
    cardWidth: Dp,
    card: Pair<FridgeItem, Ingredient>,
    userViewModel: UserViewModel
) {
  var showEditDialog by remember { mutableStateOf(false) }
  var updatedQuantity by remember { mutableStateOf(card.first.quantity) }

  Column(
      modifier = Modifier.padding(8.dp).width(cardWidth),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
        Card(
            modifier = Modifier.padding(8.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary),
            shape = RoundedCornerShape(CARD_BORDER_ROUND.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
              Box(modifier = Modifier.fillMaxSize()) {
                // Pencil Icon - Positioned at Top Left
                IconButton(
                    onClick = { showEditDialog = true },
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(20.dp),
                ) {
                  Icon(
                      imageVector = Icons.Default.Edit,
                      contentDescription = "Edit ${card.second.name} Quantity",
                      tint = MaterialTheme.colorScheme.onSecondary,
                  )
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                      Row(
                          modifier =
                              Modifier.padding(8.dp, 8.dp, 8.dp, 8.dp)
                                  .background(
                                      color = tagBackground, shape = RoundedCornerShape(16.dp)),
                          verticalAlignment = Alignment.CenterVertically,
                          horizontalArrangement = Arrangement.Center) {
                            Text(
                                modifier =
                                    Modifier.testTag("${card.second.name} Quantity")
                                        .padding(horizontal = 12.dp, vertical = 4.dp),
                                text = "${card.first.quantity} x ${card.second.quantity}",
                                fontSize = 14.sp,
                                color = Color.White // Text color
                                )
                          }
                      Image(
                          painter =
                              rememberAsyncImagePainter(
                                  model = card.second.images[PRODUCT_FRONT_IMAGE_THUMBNAIL_URL]),
                          contentDescription = stringResource(R.string.recipe_image),
                          modifier =
                              Modifier.size(100.dp)
                                  .testTag(RECIPE_IMAGE_1)
                                  .clip(
                                      RoundedCornerShape(
                                          INGREDIENT_DISPLAY_IMAGE_BORDER_RADIUS.dp)),
                          contentScale = ContentScale.Fit)

                      ExpirationBar(expirationDate = card.first.expirationDate)
                    }
              }
            }
        Text(
            modifier = Modifier.padding(vertical = 2.dp, horizontal = 8.dp),
            text = card.second.name,
            style = MaterialTheme.typography.titleMedium,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis)

        val formattedDate =
            card.first.expirationDate.format(
                DateTimeFormatter.ofPattern(stringResource(R.string.date_pattern)))

        Text(
            modifier = Modifier.padding(vertical = 2.dp, horizontal = 8.dp),
            text = formattedDate,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))

        // Edit Quantity Popup Dialog
        if (showEditDialog) {
          UpdateQuantityDialog(
              updatedQuantity,
              hiddeEditDialog = { showEditDialog = false },
              setUpdatedQuantity = { updatedQuantity = it },
              userViewModel = userViewModel)
        }
      }
}

@Composable
private fun UpdateQuantityDialog(
    updatedQuantity: Int,
    hiddeEditDialog: () -> Unit,
    setUpdatedQuantity: (Int) -> Unit,
    userViewModel: UserViewModel
) {
  Dialog(onDismissRequest = hiddeEditDialog) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.padding(16.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary)) {
          Column(
              modifier = Modifier.padding(16.dp),
              horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Edit Quantity",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 16.dp))

                // Input Field for Quantity
                /*OutlinedTextField(
                value = updatedQuantity,
                onValueChange = { setUpdatedQuantity(it) },
                label = { Text("Quantity") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors =
                    TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        unfocusedIndicatorColor =
                            MaterialTheme.colorScheme.onSecondaryContainer,
                        cursorColor =
                            MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),
                        focusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        focusedTextColor =
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        unfocusedTextColor =
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    ))*/
                // Quantity Adjust Buttons
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                      Button(
                          onClick = {
                            if (updatedQuantity > 0) setUpdatedQuantity(updatedQuantity - 1)
                          },
                          modifier = Modifier.size(48.dp),
                          colors =
                              ButtonDefaults.buttonColors(
                                  MaterialTheme.colorScheme.onSecondaryContainer)) {
                            Text(
                                text = "-",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White)
                          }

                      Spacer(modifier = Modifier.width(24.dp))

                      Text(
                          text = updatedQuantity.toString(),
                          style = MaterialTheme.typography.bodyLarge,
                          fontSize = 20.sp,
                          color = MaterialTheme.colorScheme.onPrimary)

                      Spacer(modifier = Modifier.width(24.dp))

                      Button(
                          onClick = { setUpdatedQuantity(updatedQuantity + 1) },
                          modifier = Modifier.size(48.dp),
                          colors =
                              ButtonDefaults.buttonColors(
                                  MaterialTheme.colorScheme.onSecondaryContainer)) {
                            Text(
                                text = "+",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White)
                          }
                    }

                Spacer(modifier = Modifier.height(16.dp))

                // Save Button
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                  TextButton(onClick = hiddeEditDialog) {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                  }
                  Spacer(modifier = Modifier.width(8.dp))
                  Button(
                      onClick = {
                        // TODO(): call quantity update
                        // userViewModel.updateFridgeItemQuantity(updatedQuantity)
                        hiddeEditDialog()
                      },
                      colors =
                          ButtonDefaults.buttonColors(
                              MaterialTheme.colorScheme.onSecondaryContainer)) {
                        Text(
                            text = "Save",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                        )
                      }
                }
              }
        }
  }
}

@Composable
fun ExpirationBar(expirationDate: LocalDate?) {
  val today = LocalDate.now()
  val daysLeft = expirationDate?.let { ChronoUnit.DAYS.between(today, it).toInt() } ?: 0

  // Define bar properties based on the days left
  val barColor =
      when {
        daysLeft > 5 -> Color(0xFF4CAF50) // Small green bar
        daysLeft in 1..5 -> Color(0xFFFFA500) // Medium orange bar
        else -> firebrickRed // Full red bar
      }
  val widthFraction =
      when {
        daysLeft >= 13 -> 0.1f
        daysLeft <= 0 -> 1.0f
        else -> (15 - daysLeft.toFloat()) / 15f
      }
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Start) {
          Box(
              modifier =
                  Modifier.fillMaxWidth()
                      .height(10.dp) // Set a consistent height for the bar
                      .clip(RoundedCornerShape(4.dp)) // Add rounded corners
                      .background(Color.LightGray) // Light cream color
              ) {
                Box(
                    modifier =
                        Modifier.fillMaxWidth(fraction = widthFraction)
                            .height(10.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(barColor)
                            .padding(horizontal = 4.dp)
                            .zIndex(1f))
              }
        }

    Text(
        text = "${max(daysLeft, 0)} day left",
        modifier = Modifier.padding(8.dp),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))
  }
}
