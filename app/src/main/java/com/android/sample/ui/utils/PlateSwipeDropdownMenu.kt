package com.android.sample.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.resources.C.Dimension.CategoryScreen.DIVIDER_ALPHA
import com.android.sample.resources.C.Dimension.CategoryScreen.DROPDOWN_HEIGHT_FRACTION
import com.android.sample.resources.C.Dimension.PADDING_16
import com.android.sample.resources.C.Dimension.PADDING_8
import com.android.sample.resources.C.TestTag.Category.DROPDOWN_CORNER_RADIUS
import com.android.sample.resources.C.TestTag.PlateSwipeDropdown.DROPDOWN
import com.android.sample.resources.C.TestTag.PlateSwipeDropdown.DROPDOWN_ITEM
import com.android.sample.resources.C.TestTag.PlateSwipeDropdown.DROPDOWN_TITLE

/**
 * A dropdown menu that displays a list of items and allows the user to select one.
 *
 * @param itemList The list of items to display in the dropdown menu.
 * @param onSelected The callback to be invoked when an item is selected. With as parameters the
 *   selected item and its index.
 * @param modifier The modifier to be applied to the dropdown menu.
 * @param defaultItemIndex The index of the item to be selected by default.
 */
@Composable
fun PlateSwipeDropdownMenu(
    itemList: List<String>,
    modifier: Modifier = Modifier,
    onSelected: (String, Int) -> Unit = { _, _ -> },
    defaultItemIndex: Int? = null
) {
  require(defaultItemIndex == null || defaultItemIndex <= itemList.size) {
    "defaultItemIndex should be less than list size"
  }

  val expanded = remember { mutableStateOf(false) }
  val selectedItem = remember {
    mutableStateOf(if (defaultItemIndex != null) itemList[defaultItemIndex] else null)
  }

  Box(modifier = modifier) {
    OutlinedButton(
        onClick = { expanded.value = !expanded.value },
        modifier = Modifier.fillMaxWidth().testTag(DROPDOWN),
        shape = RoundedCornerShape(DROPDOWN_CORNER_RADIUS.dp),
        colors =
            ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface)) {
          Text(
              text = selectedItem.value ?: stringResource(R.string.no_item_selected),
              style = MaterialTheme.typography.bodyMedium,
              modifier = Modifier.padding(vertical = PADDING_8.dp).testTag(DROPDOWN_TITLE))
        }

    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false },
        modifier =
            Modifier.padding(horizontal = PADDING_16.dp)
                .fillMaxWidth()
                .fillMaxHeight(DROPDOWN_HEIGHT_FRACTION)
                .background(MaterialTheme.colorScheme.surface)) {
          itemList.forEachIndexed { index, selectedText ->
            DropdownMenuItem(
                text = { Text(text = selectedText, style = MaterialTheme.typography.bodyMedium) },
                onClick = {
                  onSelected(selectedText, index)
                  selectedItem.value = selectedText
                  expanded.value = false
                },
                modifier = Modifier.testTag(DROPDOWN_ITEM))
            if (index < itemList.size - 1) {
              HorizontalDivider(
                  color = MaterialTheme.colorScheme.onSurface.copy(alpha = DIVIDER_ALPHA))
            }
          }
        }
  }
}
