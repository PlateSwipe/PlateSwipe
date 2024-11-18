package com.android.sample.ui.utils

import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.user.User

val testRecipes: List<Recipe> =
    listOf(
        Recipe(
            uid = "1",
            name = "Pasta Bolognese",
            category = "Beef",
            origin = "Italy",
            instructions =
                "1. Boil water\n2. Add pasta\n3. Cook for 10 minutes\n4. Drain water\n5. Add sauce",
            strMealThumbUrl =
                "https://www.recipetineats.com/penne-all-arrabbiata-spicy-tomato-pasta/",
            ingredientsAndMeasurements =
                listOf(
                    Pair("Beef", "1 lb"),
                    Pair("Pasta", "1 lb"),
                    Pair("Tomato Sauce", "1 cup"),
                ),
            time = "30 min",
        ),
        Recipe(
            uid = "2",
            name = "Chicken Curry",
            category = "Chicken",
            origin = "India",
            instructions =
                "1. Boil water\n2. Add chicken\n3. Cook for 10 minutes\n4. Drain water\n5. Add sauce",
            strMealThumbUrl = "https://www.foodfashionparty.com/2023/08/05/everyday-chicken-curry/",
            ingredientsAndMeasurements =
                listOf(
                    Pair("Chicken", "1 lb"),
                    Pair("Curry Powder", "1 tbsp"),
                    Pair("Coconut milk", "1 cup"),
                ),
            time = "40 min",
        ),
        Recipe(
            uid = "3",
            name = "Fish and Chips",
            category = "Seafood",
            origin = "United Kingdom",
            instructions =
                "1. Boil water\n2. Add fish\n3. Cook for 10 minutes\n4. Drain water\n5. Add chips",
            strMealThumbUrl =
                "https://3.zwcdn.zwift.com.au/ProductCatalogue/8090/ManorLakesFishChips_Fish-Chips-Deal.jpg",
            ingredientsAndMeasurements =
                listOf(
                    Pair("Fish", "1 lb"),
                    Pair("Potatoes", "1 lb"),
                    Pair("Tartar Sauce", "1 cup"),
                ),
            time = "15 min",
        ),
        Recipe(
            uid = "4",
            name = "Salad",
            instructions = "take it out of the bag and shove it in your mouth",
            strMealThumbUrl = "https://www.clovegarden.com/recipes/img/ygv_salisabl1c.jpg",
            ingredientsAndMeasurements =
                listOf(
                    Pair("Lettuce", "1 head"),
                ),
        ))

val testIngredients: List<Ingredient> =
    listOf(
        Ingredient(
            uid = "1",
            barCode = 584935L,
            name = "Apple",
            brands = "Apple Co",
            quantity = "1 apple",
            categories = listOf("Healthy", "Fruit", "Vegan"),
            images = listOf("display_normal", "display_small", "display_thumbnail")),
        Ingredient(
            uid = "2",
            barCode = 4325435L,
            name = "Dark Chocolate Bar",
            brands = "Linted",
            quantity = "200 g",
            categories = listOf("Gourmandise", "Chocolate", "Snack", "Dessert"),
            images = listOf("display_normal", "display_small", "display_thumbnail")),
        Ingredient(name = "Mystery Goo", categories = emptyList(), images = emptyList()),
    )

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
