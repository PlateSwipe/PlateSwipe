package com.android.sample

import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.junit.Before

class MyServiceTest {

    private lateinit var myService: MyService

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        myService = mock(MyService::class.java)
    }

    @Test
    fun testPerformAction() {
        // Arrange
        `when`(myService.performAction()).thenReturn("Mocked Action")

        // Act
        val result = myService.performAction()

        // Assert
        assert(result == "Mocked Action")
        verify(myService).performAction()
    }
}