package com.android.sample.model.recipe

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.android.sample.R

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
          "fire" -> R.drawable.fire
          "salt" -> R.drawable.salt
          "mortar" -> R.drawable.mortar
          "axe" -> R.drawable.axe
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
          "fire" -> R.string.fire_icon_description
          "salt" -> R.string.salt_icon_description
          "mortar" -> R.string.mortar_icon_description
          "axe" -> R.string.axe_icon_description
          else -> R.string.fire_icon_description // Default to fire description if unknown
        }
}
