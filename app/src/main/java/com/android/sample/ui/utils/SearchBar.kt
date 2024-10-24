package com.android.sample.ui.utils

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun SearchBar(modifier: Modifier = Modifier, onValueChange: (String) -> Unit = {}) {
  var searchText by remember { mutableStateOf("") }

  TextField(
      value = searchText,
      onValueChange = {
        searchText = it
        onValueChange(it)
      },
      modifier = modifier.testTag("searchBar"),
      shape = RoundedCornerShape(16.dp),
      leadingIcon = {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = "search",
            tint = MaterialTheme.colorScheme.onPrimary)
      },
      placeholder = { Text(text = "Search", color = MaterialTheme.colorScheme.onPrimary) },
      colors =
          TextFieldDefaults.colors(
              focusedContainerColor = MaterialTheme.colorScheme.onSecondary,
          ))
}
