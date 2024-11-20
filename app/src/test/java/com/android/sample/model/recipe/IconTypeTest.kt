package com.android.sample.model.recipe

import com.android.sample.resources.C.Tag.ICON_AXE
import com.android.sample.resources.C.Tag.ICON_FIRE
import com.android.sample.resources.C.Tag.ICON_MORTAR
import com.android.sample.resources.C.Tag.ICON_SALT
import org.junit.Assert.assertEquals
import org.junit.Test

class IconTypeTest {

  companion object {
    const val FIRE_ICON_RES = 2131165362
    const val SALT_ICON_RES = 2131165483
    const val MORTAR_ICON_RES = 2131165426
    const val AXE_ICON_RES = 2131165315
    const val CHEF_S_HAT_RES = 2131165329

    const val FIRE_DESCRIPTION_RES = 2131820659
    const val SALT_DESCRIPTION_RES = 2131820966
    const val MORTAR_DESCRIPTION_RES = 2131820869
    const val AXE_DESCRIPTION_RES = 2131820589
    const val CHEF_HAT_DESCRIPTION_RES = 2131820608
  }

  @Test
  fun `test valid icon name fire`() {
    val iconType = IconType(ICON_FIRE)

    // Assert that iconResId and descriptionResId return correct values for fire
    assertEquals(FIRE_ICON_RES, iconType.iconResId)
    assertEquals(FIRE_DESCRIPTION_RES, iconType.descriptionResId)
  }

  @Test
  fun `test valid icon name salt`() {
    val iconType = IconType(ICON_SALT)

    // Assert that iconResId and descriptionResId return correct values for salt
    assertEquals(SALT_ICON_RES, iconType.iconResId)
    assertEquals(SALT_DESCRIPTION_RES, iconType.descriptionResId)
  }

  @Test
  fun `test valid icon name mortar`() {
    val iconType = IconType(ICON_MORTAR)

    // Assert that iconResId and descriptionResId return correct values for mortar
    assertEquals(MORTAR_ICON_RES, iconType.iconResId)
    assertEquals(MORTAR_DESCRIPTION_RES, iconType.descriptionResId)
  }

  @Test
  fun `test valid icon name axe`() {
    val iconType = IconType(ICON_AXE)

    // Assert that iconResId and descriptionResId return correct values for axe
    assertEquals(AXE_ICON_RES, iconType.iconResId)
    assertEquals(AXE_DESCRIPTION_RES, iconType.descriptionResId)
  }

  @Test
  fun `test invalid icon name returns default hat`() {
    val iconType = IconType("unknown")

    // Assert that iconResId and descriptionResId return default values for unknown name
    assertEquals(CHEF_S_HAT_RES, iconType.iconResId)
    assertEquals(CHEF_HAT_DESCRIPTION_RES, iconType.descriptionResId)
  }

  @Test
  fun `test empty icon name returns default hat`() {
    val iconType = IconType("")

    // Assert that iconResId and descriptionResId return default values for empty name
    assertEquals(CHEF_S_HAT_RES, iconType.iconResId)
    assertEquals(CHEF_HAT_DESCRIPTION_RES, iconType.descriptionResId)
  }
}
