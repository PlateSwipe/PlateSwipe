package com.android.sample.ui.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.recipe.Recipe
import com.android.sample.resources.C.Tag.SEARCH_BAR_PLACE_HOLDER

/**
 * A search bar that allows users to search for recipes.
 *
 * @param modifier the modifier to apply to this layout node.
 * @param onValueChange the callback to invoke when the search text changes.
 */
@Preview
@Composable
fun SearchBar(modifier: Modifier = Modifier, list: List<Recipe> = emptyList()) {
  var searchText by remember { mutableStateOf("") }
  var isFocused by remember { mutableStateOf(false) }
  Row(
      horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
      verticalAlignment = Alignment.CenterVertically,
      modifier = modifier.testTag("searchBar")) {
        Image(
            painter = painterResource(id = R.drawable.search),
            contentDescription = "search icon",
            modifier =
                Modifier.padding(0.dp).width(20.2643.dp).height(20.72197.dp).testTag("search icon"))
        TextField(
            value = if (isFocused) "$searchText|" else searchText,
            onValueChange = {
              if (isFocused) searchText = it.dropLast(1) else searchText = it
              filter(searchText, list)
            },
            textStyle =
                TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontFamily = FontFamily(Font(R.font.montserrat_light)),
                    fontWeight = FontWeight(400),
                    color = Color(0xFF131313),
                ),
            placeholder = { Text(SEARCH_BAR_PLACE_HOLDER, fontSize = 16.sp) },
            modifier =
                Modifier.width(253.dp).height(40.dp).onFocusChanged { focusState ->
                  isFocused = focusState.isFocused
                })
      }
}

/**
 * Filters the list of recipes based on the search string.
 *
 * @param string the search string to filter the list of recipes.
 * @param list the list of recipes to filter.
 * @return the filtered list of recipes.
 */
fun filter(string: String, list: List<Recipe>): List<Recipe> {
  return list.filter { it.strMeal.contains(string) }
}
