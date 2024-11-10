package com.android.sample.ui.createRecipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.android.sample.R
import com.android.sample.resources.C.Tag.CHEF_IMAGE_CORNER_RADIUS

@Composable
fun ChefImage(modifier: Modifier = Modifier) {
  Image(
      painter = painterResource(id = R.drawable.chef_standing),
      contentDescription = stringResource(R.string.chef_standing_description),
      modifier = modifier.clip(RoundedCornerShape(CHEF_IMAGE_CORNER_RADIUS)),
      contentScale = ContentScale.FillBounds)
}
