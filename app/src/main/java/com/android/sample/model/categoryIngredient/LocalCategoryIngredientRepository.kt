package com.android.sample.model.categoryIngredient

import android.content.Context
import com.android.sample.R
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets

class LocalCategoryIngredientRepository(val context: Context) : CategoryIngredientRepository {
  override fun searchCategory(
      query: String,
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit,
      count: Int
  ) {
    try {
      val inputStream: InputStream = context.resources.openRawResource(R.raw.cleaned_categories)
      val inputString = inputStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
      val categories = inputString.split("\n")
      val filteredCategories =
          categories
              .filter { it.contains(query, ignoreCase = true) }
              .sortedBy { it.count() }
              .take(count)
              .map { it.filter { c -> c != '\r' } }
      onSuccess(filteredCategories)
    } catch (e: IOException) {
      onFailure(e)
    }
  }
}
