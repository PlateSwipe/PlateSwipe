package com.android.sample

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.resources.C.Tag.main_screen_container
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest : TestCase() {
    @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        Intents.init()
    }

    // Release Intents after each test
    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun mainScreenLoadsSuccessfullyTest(){
        composeTestRule.onNodeWithTag(main_screen_container).assertExists()
    }
}