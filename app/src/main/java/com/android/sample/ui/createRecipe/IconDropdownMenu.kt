package com.android.sample.ui.createRecipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.recipe.IconType
import com.android.sample.resources.C.Tag.HORIZONTAL_PADDING_ICON_DROPDOWN
import com.android.sample.resources.C.Tag.ICON_AXE
import com.android.sample.resources.C.Tag.ICON_CHEF_HAT
import com.android.sample.resources.C.Tag.ICON_FIRE
import com.android.sample.resources.C.Tag.ICON_MORTAR
import com.android.sample.resources.C.Tag.ICON_SALT
import com.android.sample.resources.C.Tag.SIZE_DROPDOWN_ICON
import com.android.sample.resources.C.Tag.VERTICAL_PADDING_ICON_DROPDOWN
import com.android.sample.ui.theme.Typography

@Composable
fun IconDropdownMenu(
    selectedIcon: IconType?,
    onIconSelected: (IconType) -> Unit,
    modifier: Modifier = Modifier
) {
  val isDropDownExpanded = remember { mutableStateOf(false) }

  val iconOptions =
      listOf(IconType(ICON_FIRE), IconType(ICON_SALT), IconType(ICON_MORTAR), IconType(ICON_AXE), IconType(ICON_CHEF_HAT))

  Box(
      modifier =
          modifier
              .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(4.dp))
              .border(1.dp, MaterialTheme.colorScheme.onSecondary, shape = RoundedCornerShape(4.dp))
              .clickable { isDropDownExpanded.value = true }
              .padding(
                  horizontal = HORIZONTAL_PADDING_ICON_DROPDOWN.dp,
                  vertical = VERTICAL_PADDING_ICON_DROPDOWN.dp)
              .testTag("IconDropdownTrigger")) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()) {
              if (selectedIcon != null) {
                Spacer(modifier = Modifier.weight(0.1f))
                Image(
                    painter = painterResource(id = selectedIcon.iconResId),
                    contentDescription = stringResource(id = selectedIcon.descriptionResId),
                    modifier = Modifier.size(SIZE_DROPDOWN_ICON.dp))
                Spacer(modifier = Modifier.weight(0.1f))
                Text(
                    text = stringResource(id = selectedIcon.descriptionResId),
                    style = Typography.bodyMedium,
                    // color of the text is black
                    color = Color.Black,
                    modifier = Modifier.weight(1f))
              } else {
                Spacer(modifier = Modifier.weight(0.05f))
                Text(
                    text = stringResource(R.string.add_icon),
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.weight(1f))
              }
              Spacer(modifier = Modifier.width(4.dp))
              Image(
                  painter = painterResource(id = R.drawable.arrow_down),
                  contentDescription = stringResource(R.string.dropdown_icon),
                  modifier = Modifier.size(SIZE_DROPDOWN_ICON.dp))
            }

        DropdownMenu(
            expanded = isDropDownExpanded.value,
            onDismissRequest = { isDropDownExpanded.value = false }) {
              iconOptions.forEach { iconType ->
                DropdownMenuItem(
                    text = {
                      Text(
                          stringResource(id = iconType.descriptionResId),
                          style = Typography.bodyMedium)
                    },
                    onClick = {
                      onIconSelected(iconType)
                      isDropDownExpanded.value = false
                    },
                    leadingIcon = {
                      Image(
                          painter = painterResource(id = iconType.iconResId),
                          contentDescription = stringResource(id = iconType.descriptionResId),
                          modifier =
                              Modifier.size(SIZE_DROPDOWN_ICON.dp)
                                  .testTag(stringResource(id = iconType.descriptionResId)))
                    })
              }
            }
      }
}
