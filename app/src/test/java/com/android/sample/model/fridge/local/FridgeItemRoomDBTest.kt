package com.android.sample.model.fridge.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.fridge.localData.FridgeItemDao
import com.android.sample.model.fridge.localData.FridgeItemDatabase
import com.android.sample.model.fridge.localData.FridgeItemEntity
import java.io.IOException
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FridgeItemRoomDBTest {

  private lateinit var fridgeItemDao: FridgeItemDao
  private lateinit var db: FridgeItemDatabase
  private lateinit var fridgeItem: FridgeItemEntity

  @Before
  fun createDB() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, FridgeItemDatabase::class.java).build()
    fridgeItemDao = db.fridgeItemDao()
    val date = LocalDate.of(2021, 10, 10)
    fridgeItem = FridgeItemEntity("id", 5, date)
  }

  @After
  @Throws(IOException::class)
  fun closeDB() {
    db.close()
  }

  @Test
  @Throws(Exception::class)
  fun testInsert() = runBlocking {
    fridgeItemDao.insert(fridgeItem)
    val allFridgeItems = fridgeItemDao.getAll()
    assert(allFridgeItems[0] == fridgeItem)
  }

  @Test
  @Throws(Exception::class)
  fun testInsertWithDifferentExpiration() = runBlocking {
    val date = LocalDate.of(2021, 10, 11)
    val fridgeItem2 = FridgeItemEntity("id", 5, date)
    fridgeItemDao.insert(fridgeItem)
    fridgeItemDao.insert(fridgeItem2)
    val allFridgeItems = fridgeItemDao.getAll()
    assert(allFridgeItems.size == 2)
  }

  @Test
  @Throws(Exception::class)
  fun testDelete() = runBlocking {
    fridgeItemDao.insert(fridgeItem)
    fridgeItemDao.delete(fridgeItem)
    val allFridgeItems = fridgeItemDao.getAll()
    assert(allFridgeItems.isEmpty())
  }

  @Test
  @Throws(Exception::class)
  fun testGetAll() = runBlocking {
    fridgeItemDao.insert(fridgeItem)
    val allFridgeItems = fridgeItemDao.getAll()
    assert(allFridgeItems.isNotEmpty())
  }
}
