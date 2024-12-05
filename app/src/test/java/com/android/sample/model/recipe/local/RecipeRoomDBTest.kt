package com.android.sample.model.recipe.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.recipe.localData.RecipeDAO
import com.android.sample.model.recipe.localData.RecipeDatabase
import com.android.sample.model.recipe.localData.RecipeEntity
import com.android.sample.model.recipe.toEntity
import com.android.sample.ui.utils.testRecipes
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import okio.IOException
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecipeRoomDBTest {

  private lateinit var recipeDAO: RecipeDAO
  private lateinit var db: RecipeDatabase
  private lateinit var recipe: RecipeEntity

  @Before
  fun createDB() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, RecipeDatabase::class.java).build()
    recipeDAO = db.recipeDao()
    recipe = testRecipes[0].toEntity()
  }

  @After
  @Throws(IOException::class)
  fun closeDB() {
    db.close()
  }

  @Test
  @Throws(Exception::class)
  fun testInsert() = runBlocking {
    recipeDAO.insert(recipe)
    val allRecipe = recipeDAO.getAll()
    assertEquals(allRecipe[0], recipe)
  }

  @Test
  @Throws(Exception::class)
  fun testUpdate() = runBlocking {
    recipeDAO.insert(recipe)
    val updateRecipe = recipe.copy(name = "Updated Name")
    recipeDAO.update(updateRecipe)
    val allIngredients = recipeDAO.getAll()
    assertEquals(allIngredients[0].name, "Updated Name")
  }

  @Test
  @Throws(Exception::class)
  fun testDelete() = runBlocking {
    recipeDAO.insert(recipe)
    recipeDAO.delete(recipe)
    val allIngredients = recipeDAO.getAll()
    assertEquals(allIngredients.size, 0)
  }

  @Test
  fun testGetAll() = runBlocking {
    for (i in 1..10) {
      recipeDAO.insert(recipe.copy(uid = i.toString()))
    }
    val allIngredients = recipeDAO.getAll()
    assertEquals(allIngredients.size, 10)
  }

  @Test
  fun testDeleteAll() = runBlocking {
    for (i in 1..10) {
      recipeDAO.insert(recipe.copy(uid = i.toString()))
    }
    recipeDAO.deleteAll()
    val allIngredients = recipeDAO.getAll()
    assertEquals(allIngredients.size, 0)
  }
}
