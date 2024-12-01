package com.android.sample.model.ingredient.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.ingredient.localData.IngredientDAO
import com.android.sample.model.ingredient.localData.IngredientDatabase
import com.android.sample.model.ingredient.localData.IngredientEntity
import com.android.sample.model.ingredient.toEntity
import com.android.sample.ui.utils.testIngredients
import junit.framework.TestCase.assertEquals
import kotlin.jvm.Throws
import kotlinx.coroutines.runBlocking
import okio.IOException
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IngredientRoomDBTest {

  private lateinit var ingredientDAO: IngredientDAO
  private lateinit var db: IngredientDatabase
  private lateinit var ingredient: IngredientEntity

  @Before
  fun createDB() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, IngredientDatabase::class.java).build()
    ingredientDAO = db.ingredientDao()
    ingredient = testIngredients[0].toEntity()
  }

  @After
  @Throws(IOException::class)
  fun closeDB() {
    db.close()
  }

  @Test
  @Throws(Exception::class)
  fun testInsert() = runBlocking {
    ingredientDAO.insert(ingredient)
    val allIngredients = ingredientDAO.getAll()
    assertEquals(allIngredients[0], ingredient)
  }

  @Test
  @Throws(Exception::class)
  fun testUpdate() = runBlocking {
    ingredientDAO.insert(ingredient)
    val updateIngredient = ingredient.copy(name = "Updated Name")
    ingredientDAO.update(updateIngredient)
    val allIngredients = ingredientDAO.getAll()
    assertEquals(allIngredients[0].name, "Updated Name")
  }

  @Test
  @Throws(Exception::class)
  fun testDelete() = runBlocking {
    ingredientDAO.insert(ingredient)
    ingredientDAO.delete(ingredient)
    val allIngredients = ingredientDAO.getAll()
    assertEquals(allIngredients.size, 0)
  }

  @Test
  fun testGetAll() = runBlocking {
    for (i in 1..10) {
      ingredientDAO.insert(ingredient.copy(uid = i.toString()))
    }
    val allIngredients = ingredientDAO.getAll()
    assertEquals(allIngredients.size, 10)
  }

  @Test
  fun testGetByBarcode() = runBlocking {
    ingredientDAO.insert(ingredient)
    val ingredientByBarcode = ingredientDAO.get(ingredient.barCode!!)
    assertEquals(ingredientByBarcode, ingredient)
  }

  @Test
  fun testDeleteAll() = runBlocking {
    for (i in 1..10) {
      ingredientDAO.insert(ingredient.copy(uid = i.toString()))
    }
    ingredientDAO.deleteAll()
    val allIngredients = ingredientDAO.getAll()
    assertEquals(allIngredients.size, 0)
  }
}
