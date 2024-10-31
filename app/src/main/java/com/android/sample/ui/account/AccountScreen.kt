package com.android.sample.ui.account

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.android.sample.R
import com.android.sample.model.user.UserViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.TopLevelDestinations
import com.android.sample.ui.theme.SampleAppTheme
import com.android.sample.ui.utils.PlateSwipeScaffold
import com.android.sample.ui.utils.RecipeList

@Composable
fun AccountScreen(navigationActions: NavigationActions, userViewModel: UserViewModel) {
    userViewModel.getCurrentUser()

    PlateSwipeScaffold(
        navigationActions = navigationActions,
        selectedItem = TopLevelDestinations.ACCOUNT.route,
        content = { padding ->

            val userName = userViewModel.userName.collectAsState()
            val profilePictureUrl = userViewModel.profilePictureUrl.collectAsState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                ProfilePicture(profilePictureUrl.value, modifier = Modifier.weight(.4f))

                Text(
                    text = userName.value ?: "User Name",
                    modifier = Modifier.weight(.1f).testTag("userName"),
                    style = MaterialTheme.typography.titleLarge
                )

                ListSelection(
                    userViewModel, modifier = Modifier.weight(.8f)
                )
            }
        }
    )
}

@Composable
private fun ListSelectionButton(
    modifier: Modifier,
    title: String,
    onClick: () -> Unit,
    isSelected: Boolean = false
) {
    Box(
        modifier = modifier
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier,
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onTertiary
        )
    }
}

@Composable
private fun ListSelection(
    userViewModel: UserViewModel, modifier: Modifier = Modifier
) {
    val likedRecipes = userViewModel.likedRecipes.collectAsState()
    val createdRecipes = userViewModel.createdRecipes.collectAsState()

    var selectedList by remember { mutableStateOf(likedRecipes) }
    var selectedListIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .height(30.dp)
                .fillMaxWidth()
        ) {
            ListSelectionButton(
                modifier = Modifier
                    .weight(1f).testTag("likedRecipesButton"),
                onClick = {
                    selectedList = likedRecipes
                    selectedListIndex = 0
                },
                title = "Liked Recipes",
                isSelected = selectedListIndex == 0
            )
            ListSelectionButton(
                modifier = Modifier
                    .weight(1f).testTag("createdRecipesButton"),
                onClick = {
                    selectedList = createdRecipes
                    selectedListIndex = 1
                },
                title = "Created Recipes",
                isSelected = selectedListIndex == 1
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(.8f),
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.onTertiary
        )

        RecipeList(
            list = selectedList.value,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun ProfilePicture(
    profilePictureUrl: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = if (profilePictureUrl.isNullOrEmpty()) {
                painterResource(id = R.drawable.account)
            } else {
                rememberAsyncImagePainter(model = profilePictureUrl)
            },
            contentDescription = "profilePicture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .testTag("profilePicture")
        )
    }
}