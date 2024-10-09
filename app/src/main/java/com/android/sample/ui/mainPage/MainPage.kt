package com.android.sample.ui.mainPage

import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATIONS
import com.android.sample.ui.navigation.NavigationActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(navigationActions: NavigationActions) {
    // Replace with actual logic to determine the selected item
    val selectedItem = navigationActions.currentRoute()
    val context = LocalContext.current
    var isTextVisible by remember { mutableStateOf(false) } // Tracks if the text should be visible

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PlateSwipe", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            BottomNavigationMenu(
                onTabSelect = { tab -> navigationActions.navigateTo(tab) },
                tabList = LIST_TOP_LEVEL_DESTINATIONS,
                selectedItem = selectedItem
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Recipe Card with Scrollable Description
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(Modifier.clickable {
                    isTextVisible = !isTextVisible
                }) {
                    GetImage(isTextVisible)

                    // Scrollable Column for Description
                    Description(isTextVisible)
                }
            }

            // Swipe Instructions
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Swipe to like or dislike the recipe.",
                color = Color.Gray,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}


@Composable
private fun GetImage(isTextVisible: Boolean) {
    Image(
        painter = painterResource(id = getNextImage()),
        contentDescription = "Recipe Image",
        modifier = Modifier
            .fillMaxWidth()
            .let { if (isTextVisible) it.height(250.dp) else it },
        contentScale = ContentScale.FillWidth
    )
}

@Composable
private fun getNextImage(): Int{
 return R.drawable.burger
}

@Composable
private fun Description(isTextVisible: Boolean) {

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Pasta",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.padding(5.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.time_line),
                contentDescription = "recipes timing"
            )
            Text(
                modifier = Modifier.padding(start = 5.dp),
                text = "30min",
                fontSize = 22.sp,

                )

        }
        if (isTextVisible) {
            Text(
                text = getRecipeDescription(),
                fontSize = 16.sp
            )
            Text(
                text = "Ingredients: Basil, Tomato, Pasta, KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK",
                fontSize = 16.sp
            )
        }

    }
}


@Composable
private fun getRecipeDescription(): String {
    return "Delicious pasta with tomato sauce. In a separate pan, prepare the sauce with garlic, onions, and fresh tomatoes. Combine the cooked pasta with the sauce, and serve hot. Enjoy your meal!"
}
