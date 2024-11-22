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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.android.sample.R
import com.android.sample.model.fridge.FridgeItem
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.ingredient.IngredientViewModel
import com.android.sample.model.user.UserViewModel
import com.android.sample.resources.C.Dimension.CreateRecipeListInstructionsScreen.CARD_BORDER_ROUND
import com.android.sample.resources.C.TestTag.Utils.TEST_TAG
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.firebrickRed
import com.android.sample.ui.theme.tagBackground
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
      content = { paddingValues -> FridgeContent(paddingValues, userViewModel) },
      showBackArrow = true)
}

@Composable
fun FridgeContent(paddingValues: PaddingValues, userViewModel: UserViewModel) {
  val listFridgeItem by userViewModel.listFridgeItems.collectAsState()
  Column(
      modifier = Modifier.fillMaxSize().padding(paddingValues),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
          item {
            listFridgeItem.chunked(2).forEach { chunk ->
              Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.Center // Distribute cards evenly
                  ) {
                    val cardWidth = 150.dp // Define a fixed width for the cards

                    // First card in the chunk
                    chunk.getOrNull(0)?.let { card1 -> ItemCard(cardWidth, card1) }

                    // Second card in the chunk (if present)
                    chunk.getOrNull(1)?.let { card2 -> ItemCard(cardWidth, card2) }

                    // Add an empty spacer if only one card exists in the chunk
                    if (chunk.size == 1) {
                      Spacer(modifier = Modifier.width(cardWidth).padding(4.dp))
                    }
                  }
            }
          }
        }
      }
}

@Composable
private fun ItemCard(cardWidth: Dp, card: Pair<FridgeItem, Ingredient>) {
  Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
    Card(
        modifier = Modifier.width(cardWidth).padding(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary),
        shape = RoundedCornerShape(CARD_BORDER_ROUND.dp)) {
          Column(
              modifier = Modifier.fillMaxSize(),
              horizontalAlignment = Alignment.CenterHorizontally) {
                card.first.quantity?.let {
                  Row(
                      modifier =
                          Modifier.padding(8.dp, 8.dp, 8.dp, 8.dp)
                              .background(color = tagBackground, shape = RoundedCornerShape(16.dp)),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.Center) {
                        Text(
                            modifier =
                                Modifier.testTag(TEST_TAG)
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                            text = it,
                            fontSize = 14.sp,
                            color = Color.White // Text color
                            )
                      }
                }

                Image(
                    painter = painterResource(id = R.drawable.chef_image_in_egg),
                    contentDescription = "wow",
                    modifier = Modifier.size(100.dp))

                ExpirationBar(expirationDate = card.first.expirationDate)
              }
        }
    Text(
        modifier = Modifier.padding(vertical = 2.dp, horizontal = 8.dp),
        text = card.second.name,
        style = MaterialTheme.typography.titleMedium,
        fontSize = 18.sp,
        color = MaterialTheme.colorScheme.onPrimary)

    val formattedDate =
        card.first.expirationDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "No Date"
    Text(
        modifier = Modifier.padding(vertical = 2.dp, horizontal = 8.dp),
        text = formattedDate,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))
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
                        Modifier.fillMaxWidth(
                                fraction = widthFraction) // Adjust bar width proportionally
                            .height(10.dp) // Match height of background
                            .clip(RoundedCornerShape(4.dp)) // Match rounded corners
                            .background(barColor)
                            .padding(horizontal = 4.dp) // Add slight spacing within the bar
                            .zIndex(1f) // Ensure it overlays properly
                    )
              }
        }

    Text(
        text = "${max(daysLeft, 0)} day left",
        modifier = Modifier.padding(8.dp),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))
  }
}
