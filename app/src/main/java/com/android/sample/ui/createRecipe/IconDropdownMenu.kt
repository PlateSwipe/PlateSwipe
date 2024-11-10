package com.android.sample.ui.createRecipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.android.sample.ui.theme.Typography

sealed class IconType(val iconResId: Int, val description: String) {
  object Fire : IconType(R.drawable.fire, "Fire")

  object Salt : IconType(R.drawable.salt, "Salt")

  object Mortar : IconType(R.drawable.mortar, "Mortar")

  object Axe : IconType(R.drawable.axe, "Axe")
}

@Composable
fun IconDropdownMenu(
    selectedIcon: IconType?,
    onIconSelected: (IconType) -> Unit,
    modifier: Modifier = Modifier
) {
  val isDropDownExpanded = remember { mutableStateOf(false) }
  val iconOptions = listOf(IconType.Fire, IconType.Salt, IconType.Mortar, IconType.Axe)

  Box(
      modifier =
          modifier
              .background(Color.White, shape = RoundedCornerShape(4.dp))
              .border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))
              .clickable { isDropDownExpanded.value = true }
              .padding(horizontal = 8.dp, vertical = 12.dp)
              .testTag("IconDropdownTrigger")) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()) {
              if (selectedIcon != null) {
                Image(
                    painter = painterResource(id = selectedIcon.iconResId),
                    contentDescription = selectedIcon.description,
                    modifier = Modifier.size(24.dp))
              } else {
                Text(
                    text = stringResource(R.string.add_icon),
                    style = Typography.bodyMedium,
                    modifier = Modifier.weight(1f))
              }
              Spacer(modifier = Modifier.width(4.dp))
              Image(
                  painter = painterResource(id = R.drawable.arrow_down),
                  contentDescription = "DropDown Icon",
                  modifier = Modifier.size(24.dp))
            }

        DropdownMenu(
            expanded = isDropDownExpanded.value,
            onDismissRequest = { isDropDownExpanded.value = false }) {
              iconOptions.forEach { iconType ->
                DropdownMenuItem(
                    text = { Text(iconType.description, style = Typography.bodyMedium) },
                    onClick = {
                      onIconSelected(iconType)
                      isDropDownExpanded.value = false
                    },
                    leadingIcon = {
                      Image(
                          painter = painterResource(id = iconType.iconResId),
                          contentDescription = iconType.description,
                          modifier = Modifier.size(24.dp).testTag(iconType.description))
                    })
              }
            }
      }
}