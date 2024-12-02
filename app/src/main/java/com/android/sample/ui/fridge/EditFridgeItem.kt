package com.android.sample.ui.fridge

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.user.UserViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.utils.Counter
import com.android.sample.ui.utils.IngredientImageBox
import com.android.sample.ui.utils.PlateSwipeScaffold
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun EditFridgeItem(navigationActions: NavigationActions, userViewModel: UserViewModel) {

  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = navigationActions.currentRoute(),
      content = { paddingValues ->
        EditComposable(paddingValues, navigationActions, userViewModel)
      },
      showBackArrow = true)
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun EditComposable(
    paddingValues: PaddingValues,
    navigationActions: NavigationActions,
    userViewModel: UserViewModel
) {

  val ingredient = userViewModel.ingredientList.collectAsState().value[0].first
  var quantity by remember { mutableIntStateOf(1) }
  var expirationDate by remember { mutableStateOf(LocalDate.now()) }

  val showDatePickerDialog = remember { mutableStateOf(false) }

  Column(
      modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Ingredient Name
        Text(
            text = ingredient.name,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onBackground)

        IngredientImageBox(ingredient)

        // Quantity Adjustment

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)) {
              Text(text = "Quantity:", style = MaterialTheme.typography.bodyMedium)
              Counter(count = quantity, onCounterChange = { quantity = it })
            }

        // Expiration Date Picker
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)) {
              Text(text = "Expiration Date:", style = MaterialTheme.typography.bodyMedium)
              Button(
                  onClick = {
                    // Open date picker dialog (you can integrate a custom or library-based date
                    // picker here)
                    // val today = LocalDate.now()
                    // expirationDate = today // Simulate date selection
                    showDatePickerDialog.value = true
                  },
                  colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)) {
                    Text(
                        text =
                            expirationDate.format(
                                DateTimeFormatter.ofPattern(stringResource(R.string.date_pattern))),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White)
                  }
            }

        Spacer(modifier = Modifier.weight(1f))

        // Save Button
        Button(
            onClick = {
              userViewModel.updateIngredientFromFridge(ingredient, quantity, expirationDate, true)

              navigationActions.navigateTo(Screen.FRIDGE)
              // userViewModel.clearIngredient()
              // userViewModel.clearSearchingIngredientList()
              // userViewModel.clearIngredientList()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)) {
              Text(text = "Save", style = MaterialTheme.typography.bodyLarge, color = Color.White)
            }
        // Expiration Date Picker Dialog
        if (showDatePickerDialog.value) {
          DatePickerDialog(
              onDismissRequest = { showDatePickerDialog.value = false },
              confirmButton = {
                TextButton(onClick = { showDatePickerDialog.value = false }) { Text("Confirm") }
              },
              dismissButton = {
                TextButton(onClick = { showDatePickerDialog.value = false }) { Text("Cancel") }
              }) {
                val state =
                    rememberDatePickerState(
                        initialSelectedDateMillis = expirationDate.toEpochDay() * 86400000L)
                DatePicker(state = state)

                DisposableEffect(state) {
                  onDispose {
                    state.selectedDateMillis?.let { millis ->
                      expirationDate = LocalDate.ofEpochDay(millis / 86400000L)
                    }
                  }
                }
              }
        }
      }
}
