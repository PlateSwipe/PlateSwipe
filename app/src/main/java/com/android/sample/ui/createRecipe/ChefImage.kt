package com.android.sample.ui.createRecipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.android.sample.R

@Composable
fun ChefImage(modifier: Modifier = Modifier, offsetX: Dp = 0.dp, offsetY: Dp = 0.dp) {
  Image(
      painter = painterResource(id = R.drawable.chef_standing),
      contentDescription = "Chef standing",
      modifier =
          modifier
              .width(250.dp)
              .height(300.dp)
              .offset(x = offsetX, y = offsetY)
              .clip(RoundedCornerShape(16.dp)),
      contentScale = ContentScale.FillBounds)
}
