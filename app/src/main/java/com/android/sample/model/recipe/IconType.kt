package com.android.sample.model.recipe

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.android.sample.R
import com.android.sample.resources.C.Tag.ICON_AXE
import com.android.sample.resources.C.Tag.ICON_FIRE
import com.android.sample.resources.C.Tag.ICON_MORTAR
import com.android.sample.resources.C.Tag.ICON_SALT

/**
 * A data class representing an icon and its associated description. The class takes an icon name as
 * a parameter and dynamically retrieves the corresponding drawable and string resource IDs.
 *
 * @property iconName The name of the icon. This is used to fetch the corresponding drawable and
 *   description resources.
 */
data class IconType(val iconName: String) {

  /**
   * Dynamically retrieves the drawable resource ID for the icon.
   *
   * This property checks the `iconName` and returns the corresponding drawable resource. If the
   * `iconName` is not recognized, it defaults to the "fire" icon.
   *
   * @return The drawable resource ID for the icon.
   */
  @get:DrawableRes
  val iconResId: Int
    get() =
        when (iconName) {
          ICON_FIRE -> R.drawable.fire
          ICON_SALT -> R.drawable.salt
          ICON_MORTAR -> R.drawable.mortar
          ICON_AXE -> R.drawable.axe
          else -> R.drawable.fire // Default to fire icon if unknown
        }

  /**
   * Dynamically retrieves the string resource ID for the description.
   *
   * This property checks the `iconName` and returns the corresponding description resource. If the
   * `iconName` is not recognized, it defaults to the "fire" description.
   *
   * @return The string resource ID for the description of the icon.
   */
  @get:StringRes
  val descriptionResId: Int
    get() =
        when (iconName) {
          ICON_FIRE -> R.string.fire_icon_description
          ICON_SALT -> R.string.salt_icon_description
          ICON_MORTAR -> R.string.mortar_icon_description
          ICON_AXE -> R.string.axe_icon_description
          else -> R.string.fire_icon_description // Default to fire description if unknown
        }
}
