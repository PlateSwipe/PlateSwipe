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
import com.android.sample.resources.C.Tag.CHEF_IMAGE_CORNER_RADIUS
import com.android.sample.resources.C.Tag.CHEF_IMAGE_HEIGHT
import com.android.sample.resources.C.Tag.CHEF_IMAGE_WIDTH

@Composable
fun ChefImage(modifier: Modifier = Modifier, offsetX: Dp = 0.dp, offsetY: Dp = 0.dp) {
  Image(
      painter = painterResource(id = R.drawable.chef_standing),
      contentDescription = "Chef standing",
      modifier =
          modifier
              .width(CHEF_IMAGE_WIDTH)
              .height(CHEF_IMAGE_HEIGHT)
              .offset(x = offsetX, y = offsetY)
              .clip(RoundedCornerShape(CHEF_IMAGE_CORNER_RADIUS)),
      contentScale = ContentScale.FillBounds)
}
