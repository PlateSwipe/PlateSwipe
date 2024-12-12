package com.android.sample.model.categoryIngredient

import android.app.Application
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
            val filteredCategories = categories.filter { it.contains(query, ignoreCase = true) }.map { Pair(it,it.indexOf(query)) }.sortedBy { it.second }.map { it.first }
            onSuccess(filteredCategories.take(count))
        } catch (e: IOException) {
            onFailure(e)
        }
    }
}