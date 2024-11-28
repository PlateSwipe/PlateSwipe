package com.android.sample.model.recipe

import androidx.test.platform.app.InstrumentationRegistry
import com.android.sample.R
import com.android.sample.resources.C.Tag.ICON_AXE
import com.android.sample.resources.C.Tag.ICON_CHEF_HAT
import com.android.sample.resources.C.Tag.ICON_FIRE
import com.android.sample.resources.C.Tag.ICON_MORTAR
import com.android.sample.resources.C.Tag.ICON_SALT
import org.junit.Assert.assertEquals
import org.junit.Test

class IconTypeTest {
  // Access the Android context to retrieve string resources
  private val context = InstrumentationRegistry.getInstrumentation().targetContext

  @Test
  fun testDescriptionResIdForKnownIcons() {
    // Check if known icon names return the correct description strings
    assertEquals(
        context.getString(R.string.fire_icon_description),
        context.getString(IconType(ICON_FIRE).descriptionResId))
    assertEquals(
        context.getString(R.string.salt_icon_description),
        context.getString(IconType(ICON_SALT).descriptionResId))
    assertEquals(
        context.getString(R.string.mortar_icon_description),
        context.getString(IconType(ICON_MORTAR).descriptionResId))
    assertEquals(
        context.getString(R.string.axe_icon_description),
        context.getString(IconType(ICON_AXE).descriptionResId))
    assertEquals(
        context.getString(R.string.chef_s_hat_icon_description),
        context.getString(IconType(ICON_CHEF_HAT).descriptionResId))
  }

  @Test
  fun testDescriptionResIdDefaultsToChefSHatForUnknownIcons() {
    // Check if unknown icon names default to chef's hat description
    assertEquals(
        context.getString(R.string.chef_s_hat_icon_description),
        context.getString(IconType("unknown_icon").descriptionResId))
  }

  @Test
  fun testIconResIdForKnownIcons() {
    // Check if known icon names return the correct drawable resources
    assertEquals(R.drawable.fire, IconType(ICON_FIRE).iconResId)
    assertEquals(R.drawable.salt, IconType(ICON_SALT).iconResId)
    assertEquals(R.drawable.mortar, IconType(ICON_MORTAR).iconResId)
    assertEquals(R.drawable.axe, IconType(ICON_AXE).iconResId)
    assertEquals(R.drawable.chef_s_hat, IconType(ICON_CHEF_HAT).iconResId)
  }

  @Test
  fun testIconResIdDefaultsToChefSHatForUnknownIcons() {
    // Check if unknown icon names default to chef's hat drawable
    assertEquals(R.drawable.chef_s_hat, IconType("unknown_icon").iconResId)
  }
}
