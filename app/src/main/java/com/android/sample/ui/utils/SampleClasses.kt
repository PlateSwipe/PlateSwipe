package com.android.sample.ui.utils

import com.android.sample.model.fridge.FridgeItem
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.recipe.Instruction
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.user.User
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_NORMAL_URL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_SMALL_URL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_THUMBNAIL_URL
import java.time.LocalDate

val recipeNames =
    arrayOf(
        "Pasta Bolognese",
        "Chicken Curry",
        "Fish and Chips",
        "Salad",
    )

val recipeCategories = arrayOf("Beef", "Chicken", "Seafood", null)

val recipeOrigins = arrayOf("Italy", "India", "United Kingdom", null)

val recipeInstructions =
    arrayOf(
        listOf(
            Instruction(description = "1. Boil water", time = "30 min", iconType = "Cook"),
            Instruction(description = "2. Add pasta", time = "30 min", iconType = "Fire"),
            Instruction(description = "3. Cook for 10 minutes", time = "30 min", iconType = "Fire"),
            Instruction(description = "4. Drain water", time = "30 min", iconType = "Fire"),
            Instruction(description = "5. Add sauce", time = "30 min", iconType = "Fire"),
        ),
        listOf(
            Instruction(description = "1. Boil water", time = "30 min", iconType = "Cook"),
            Instruction(description = "2. Add chicken", time = "30 min", iconType = "Fire"),
            Instruction(description = "3. Cook for 10 minutes", time = "30 min", iconType = "Fire"),
            Instruction(description = "4. Drain water", time = "30 min", iconType = "Fire"),
            Instruction(description = "5. Add sauce", time = "30 min", iconType = "Fire"),
        ),
        listOf(
            Instruction(description = "1. Boil water", time = "30 min", iconType = "Cook"),
            Instruction(description = "2. Add fish", time = "30 min", iconType = "Fire"),
            Instruction(description = "3. Cook for 10 minutes", time = "30 min", iconType = "Fire"),
            Instruction(description = "4. Drain water", time = "30 min", iconType = "Fire"),
            Instruction(description = "5. Add chips", time = "30 min", iconType = "Fire"),
        ),
        listOf(
            Instruction(
                description = "take it out of the bag and shove it in your mouth",
                time = null,
                iconType = "Fire"),
        ))

val recipeStrMealThumbUrls =
    arrayOf(
        "https://www.recipetineats.com/penne-all-arrabbiata-spicy-tomato-pasta/",
        "https://www.foodfashionparty.com/2023/08/05/everyday-chicken-curry/",
        "https://3.zwcdn.zwift.com.au/ProductCatalogue/8090/ManorLakesFishChips_Fish-Chips-Deal.jpg",
        "https://www.clovegarden.com/recipes/img/ygv_salisabl1c.jpg",
    )

val recipeIngredientLists =
    arrayOf(
        listOf(
            Pair("Beef", "1 lb"),
            Pair("Pasta", "1 lb"),
            Pair("Tomato Sauce", "1 cup"),
        ),
        listOf(
            Pair("Chicken", "1 lb"),
            Pair("Curry Powder", "1 tbsp"),
            Pair("Coconut milk", "1 cup"),
        ),
        listOf(
            Pair("Fish", "1 lb"),
            Pair("Potatoes", "1 lb"),
            Pair("Tartar Sauce", "1 cup"),
        ),
        listOf(
            Pair("Lettuce", "1 head"),
        ),
    )

val recipeTimes = arrayOf("30 min", "40 min", "15 min", null)

val testRecipes =
    recipeNames.mapIndexed { index, name ->
      Recipe(
          uid = (index + 1).toString(),
          name = name,
          category = recipeCategories[index],
          origin = recipeOrigins[index],
          instructions = recipeInstructions[index],
          strMealThumbUrl = recipeStrMealThumbUrls[index],
          ingredientsAndMeasurements = recipeIngredientLists[index],
          time = recipeTimes[index],
          url =
              "https://firebasestorage.googleapis.com/v0/b/plateswipe.appspot.com/o/images%2Frecipe%2F0Y7fBUeqH6aq2P2gcHtA%2FMain.jpg?alt=media&token=cb99e609-a846-42a6-b71c-b9b3d160ff9b")
    }

val ingredientNames =
    arrayOf(
        "Apple",
        "Dark Chocolate Bar",
        "Mystery Goo",
    )

val ingredientBrands = arrayOf("Apple Co", "Linted", null)

val ingredientQuantities = arrayOf("1 apple", "200 g", null)

val ingredientFridgeItemQuantities = arrayOf(1, 200, 0)

val ingredientCategories =
    arrayOf(
        listOf("Healthy", "Fruit", "Vegan"),
        listOf("Gourmandise", "Chocolate", "Snack", "Dessert"),
        emptyList(),
    )

val ingredientImages =
    mutableMapOf(
        PRODUCT_FRONT_IMAGE_NORMAL_URL to "https://display_normal",
        PRODUCT_FRONT_IMAGE_THUMBNAIL_URL to "https://display_thumbnail",
        PRODUCT_FRONT_IMAGE_SMALL_URL to "https://display_small")

val testIngredients =
    ingredientNames.mapIndexed { index, name ->
      Ingredient(
          uid = (index + 1).toString(),
          barCode = index.toLong(),
          name = name,
          brands = ingredientBrands[index],
          quantity = ingredientQuantities[index],
          categories = ingredientCategories[index],
          images = ingredientImages,
      )
    }
val testIngredientsNullCategory =
    ingredientNames.mapIndexed { index, name ->
      Ingredient(
          uid = (index + 1).toString(),
          barCode = index.toLong(),
          name = name,
          brands = ingredientBrands[index],
          categories = ingredientCategories[index],
          images = ingredientImages,
      )
    }

val ingredientExpirationDateExample: LocalDate = LocalDate.of(2024, 11, 25)

val ingredientExpirationDateModifiedExample: LocalDate = LocalDate.of(2024, 11, 26)

const val ingredientQuantityExample: Int = 500

val testFridgeItem =
    testIngredients.mapIndexed { index, ingredient ->
      FridgeItem(
          ingredient.barCode.toString(),
          ingredientFridgeItemQuantities[index],
          ingredientExpirationDateExample)
    }

val testFridgeItemModifiedExpirationDate =
    testIngredients.mapIndexed { index, ingredient ->
      FridgeItem(
          ingredient.barCode.toString(),
          ingredientFridgeItemQuantities[index],
          ingredientExpirationDateModifiedExample)
    }

val testFridgeItemModifiedQuantity =
    testIngredients.map { ingredient ->
      FridgeItem(
          ingredient.barCode.toString(), ingredientQuantityExample, ingredientExpirationDateExample)
    }

val testUsers: List<User> =
    listOf(
        User(
            "001",
            "Gigel Frone",
            "",
            listOf(testFridgeItem[0]),
            listOf(testRecipes[0].uid),
            listOf(testRecipes[1].uid),
            "25/01/2002"),
        User("002", "Ion Popescu", "", emptyList(), emptyList(), emptyList(), ""),
    )
