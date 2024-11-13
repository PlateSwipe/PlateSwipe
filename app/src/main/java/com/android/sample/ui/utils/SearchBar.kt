package com.android.sample.ui.utils

import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.resources.C.Tag.SEARCH_BAR_CORNER_RADIUS

/**
 * A search bar that allows users to search for recipes.
 *
 * @param modifier the modifier to apply to this layout node.
 * @param onValueChange the callback to invoke when the search text changes.
 */
@Preview
@Composable
fun SearchBar(modifier: Modifier = Modifier, onValueChange: (String) -> Unit = {}, isSingleLine: Boolean = false) {
    var searchText by remember { mutableStateOf("") }

    TextField(
        value = searchText,
        onValueChange = {
            searchText = it
            onValueChange(it)
        },
        modifier =
        modifier
            .testTag("searchBar")
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(SEARCH_BAR_CORNER_RADIUS.dp),
            ).fillMaxWidth(),
        shape = RoundedCornerShape(SEARCH_BAR_CORNER_RADIUS.dp),
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "searchIcon",
                tint = MaterialTheme.colorScheme.onPrimary)
        },
        placeholder = {
            Text(
                text = stringResource(R.string.search_bar_place_holder),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
        },
        colors =
        TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.onSecondary,

            // we need to make these transparent or a weird line appears
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        singleLine = isSingleLine)
}
