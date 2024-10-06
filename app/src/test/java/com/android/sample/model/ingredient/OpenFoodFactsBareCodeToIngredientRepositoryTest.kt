package com.android.sample.model.ingredient

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.kaspersky.components.kautomator.common.Environment
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.robolectric.RobolectricTestRunner
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
class OpenFoodFactsBareCodeToIngredientRepositoryTest (
) {

    private lateinit var client: OkHttpClient

    private lateinit var repository: OpenFoodFactsIngredientRepository

    @Before
    fun setUp() {
        client = OkHttpClient()
        repository = OpenFoodFactsIngredientRepository(client)
    }

    @Test
    fun testGetReturnsValue() {
        val barCode = 4008400290126
        var ingredient: Ingredient? = null
        repository.get(barCode, { ingredient = it }, { throw it })
        Thread.sleep(5000)
        assertEquals("Kinder Pingui 30", ingredient?.name)
    }

}