package com.android.sample.ui.utils

import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.user.User

val recipeNames = arrayOf(
    "Pasta Bolognese",
    "Chicken Curry",
    "Fish and Chips",
    "Salad",
)

val recipeCategories = arrayOf(
    "Beef",
    "Chicken",
    "Seafood",
    null
)

val recipeOrigins = arrayOf(
    "Italy",
    "India",
    "United Kingdom",
    null
)

val recipeInstructions = arrayOf(
    "1. Boil water\n2. Add pasta\n3. Cook for 10 minutes\n4. Drain water\n5. Add sauce",
    "1. Boil water\n2. Add chicken\n3. Cook for 10 minutes\n4. Drain water\n5. Add sauce",
    "1. Boil water\n2. Add fish\n3. Cook for 10 minutes\n4. Drain water\n5. Add chips",
    "take it out of the bag and shove it in your mouth",)

val recipeStrMealThumbUrls = arrayOf(
    "https://www.recipetineats.com/penne-all-arrabbiata-spicy-tomato-pasta/",
    "https://www.foodfashionparty.com/2023/08/05/everyday-chicken-curry/",
    "https://3.zwcdn.zwift.com.au/ProductCatalogue/8090/ManorLakesFishChips_Fish-Chips-Deal.jpg",
    "https://www.clovegarden.com/recipes/img/ygv_salisabl1c.jpg",
)

val recipeIngredientLists = arrayOf(
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

val recipeTimes = arrayOf(
    "30 min",
    "40 min",
    "15 min",
    null
)

val testRecipes = recipeNames.mapIndexed { index, name ->
    Recipe(
        uid = (index + 1).toString(),
        name = name,
        category = recipeCategories[index],
        origin = recipeOrigins[index],
        instructions = recipeInstructions[index],
        strMealThumbUrl = recipeStrMealThumbUrls[index],
        ingredientsAndMeasurements = recipeIngredientLists[index],
        time = recipeTimes[index],
    )
}

val ingredientNames = arrayOf(
    "Apple",
    "Dark Chocolate Bar",
    "Mystery Goo",
)

val ingredientBrands = arrayOf(
    "Apple Co",
    "Linted",
    null
)

val ingredientQuantities = arrayOf(
    "1 apple",
    "200 g",
    null
)

val ingredientCategories = arrayOf(
    listOf("Healthy", "Fruit", "Vegan"),
    listOf("Gourmandise", "Chocolate", "Snack", "Dessert"),
    emptyList(),
)

val ingredientImages = arrayOf(
    listOf("display_normal", "display_small", "display_thumbnail"),
    listOf("display_normal", "display_small", "display_thumbnail"),
    emptyList(),
)

val testIngredients = ingredientNames.mapIndexed { index, name ->
    Ingredient(
        barCode = index.toLong(),
        name = name,
        brands = ingredientBrands[index],
        quantity = ingredientQuantities[index],
        categories = ingredientCategories[index],
        images = ingredientImages[index],
    )
}

val testUsers: List<User> =
    listOf(
        User(
            "001",
            "Gigel Frone",
            "",
            listOf(Pair(testIngredients[0].barCode.toString(), 1)),
            listOf(testRecipes[0].uid),
            listOf(testRecipes[1].uid)),
        User("002", "Ion Popescu", "", emptyList(), emptyList(), emptyList()),
    )
