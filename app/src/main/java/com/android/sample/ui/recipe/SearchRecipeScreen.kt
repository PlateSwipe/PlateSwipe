package com.android.sample.ui.recipeOverview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.recipe.RecipesViewModel
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATIONS
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.utils.*

@Composable
fun SearchRecipeScreen(navigationActions: NavigationActions, recipesViewModel: RecipesViewModel) {
    val selectedItem = navigationActions.currentRoute()
    val currentRecipes by recipesViewModel.recipes.collectAsState()
    var recipes by remember { mutableStateOf(currentRecipes) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            Row(
            modifier = Modifier
                .border(width = 1.dp, color = Color(0xFF000000))
                .border(width = 1.dp, color = Color(0x33000000))
                .width(412.dp)
                .height(26.dp)
                .background(color = Color(0xFFFFFFFF)),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = painterResource(id = R.drawable.plateswipelogo),
                contentDescription = "PlateSwipe logo",
                modifier = Modifier
                    .padding(0.dp)
                    .width(24.dp)
                    .height(24.dp)
            )
            Text(
                text = "PlateSwipe",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                    fontWeight = FontWeight(600),
                    color = Color(0xFF1B263B),
                ),
                modifier = Modifier
                    .width(118.dp)
                    .height(24.dp)
            )

        }},
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
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically,
            ){
                SearchingBar(recipes)
                Image(
                    painter = painterResource(id = R.drawable.filter),
                    contentDescription = "image description",
                    contentScale = ContentScale.None,
                    modifier = Modifier
                        .padding(0.dp)
                        .width(24.dp)
                        .height(24.dp)
                        .clickable { navigationActions.navigateTo("Filter Screen") }
                )
            }

            RecipeList(
                list = recipes,
                onRecipeSelected = { recipe ->
                    recipesViewModel.updateCurrentRecipe(recipe)
                    navigationActions.navigateTo("Overview Recipe Screen")
                 },
                topCornerButton = { recipe -> TopCornerLikeButton(recipe) }
            )
        }
    }
}

@Composable
fun SearchingBar(recipes : List<Recipe> ) {
    Column(
        verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.shadow(elevation = 4.dp, spotColor = Color(0x40000000), ambientColor = Color(0x40000000))
            .shadow(elevation = 4.dp, spotColor = Color(0x40000000), ambientColor = Color(0x40000000))
            .border(width = 1.dp, color = Color(0xFF000000))
            .border(width = 1.dp, color = Color(0x33000000))
            .width(329.dp)
            .height(64.dp)
            .background(color = Color(0xFFFFFFFF))
            .background(color = Color(0x33000000))
            .padding(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 16.dp)
    ){
       SearchBar(Modifier
           .shadow(elevation = 4.dp, spotColor = Color(0x0F808080), ambientColor = Color(0x0F808080))
           .shadow(elevation = 4.dp, spotColor = Color(0x1A808080), ambientColor = Color(0x1A808080))
           .border(width = 1.dp, color = Color(0xFFF2F2F2), shape = RoundedCornerShape(size = 16.dp))
           .padding(0.5.dp)
           .width(305.dp)
           .height(40.dp)
           .background(color = Color(0xFFF8F8F8), shape = RoundedCornerShape(size = 16.dp))
           .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 8.dp), recipes)
    }
}

@Composable
fun FilterChip(text: String) {
    OutlinedButton(
        onClick = { /* Apply filter logic */ },
        modifier = Modifier,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
    ) {
        Text(text, fontSize = 12.sp)
    }
}

@Composable
fun ClearAllChip() {
    Button(
        onClick = { /* Clear all filters logic */ },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text("Clear All", fontSize = 12.sp)
    }
}
