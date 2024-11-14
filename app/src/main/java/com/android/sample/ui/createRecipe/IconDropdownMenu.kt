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

sealed class IconType(val iconResId: Int, val descriptionResId: Int) {
  object Fire : IconType(R.drawable.fire, R.string.fire_icon_description)

  object Salt : IconType(R.drawable.salt, R.string.salt_icon_description)

  object Mortar : IconType(R.drawable.mortar, R.string.mortar_icon_description)

  object Axe : IconType(R.drawable.axe, R.string.axe_icon_description)
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
              .border(1.dp, Color.DarkGray, shape = RoundedCornerShape(4.dp))
              .clickable { isDropDownExpanded.value = true }
              .padding(horizontal = 8.dp, vertical = 19.dp)
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
                    modifier = Modifier.size(24.dp))
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
                    color = Color.DarkGray,
                    modifier = Modifier.weight(1f))
              }
              Spacer(modifier = Modifier.width(4.dp))
              Image(
                  painter = painterResource(id = R.drawable.arrow_down),
                  contentDescription = stringResource(R.string.dropdown_icon),
                  modifier = Modifier.size(24.dp))
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
                              Modifier.size(24.dp)
                                  .testTag(stringResource(id = iconType.descriptionResId)))
                    })
              }
            }
      }
}
