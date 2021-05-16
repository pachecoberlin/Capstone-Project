package de.pacheco.bakingapp.utils

import de.pacheco.bakingapp.activities.RecipeListFragment
import de.pacheco.bakingapp.model.Recipe
import de.pacheco.bakingapp.model.Step

fun getStep(steps: List<Step>, stepId: Int): Step? {
    return steps.find { it.id == stepId }
}

fun getNextStep(actualStepId: Int, recipe: Recipe): Step {
    return getStep(actualStepId, recipe, true)
}

fun getPreviousStep(actualStepId: Int, recipe: Recipe): Step {
    return getStep(actualStepId, recipe, false)
}

private fun getStep(oldStepID: Int, recipe: Recipe, next: Boolean): Step {
    var newStepId = oldStepID
    var step: Step?
    do {
        newStepId = when {
            next && newStepId + 1 >= maxStep(recipe) -> maxStep(recipe)
            !next && newStepId - 1 < minStep(recipe) -> minStep(recipe)
            next -> newStepId + 1
            !next -> newStepId - 1
            else -> return recipe.steps.first()
        }
        step = getStep(recipe.steps, newStepId)
    } while (step == null)
    return step
}

private fun minStep(recipe: Recipe): Int {
    return recipe.steps.minByOrNull { it.id }?.id ?: Int.MAX_VALUE
}

private fun maxStep(recipe: Recipe): Int {
    return recipe.steps.maxByOrNull { it.id }?.id ?: Int.MIN_VALUE
}

fun getIngredients(recipe: Recipe): String {
    return "Ingredients:\n\n" + recipe.ingredients.joinToString(separator = "") { it.toString() }
}

fun getNextRecipe(recipe: Recipe): Recipe {
    val recipes = RecipeListFragment.recipes
    val nextIndex = recipes.indexOf(recipe) + 1
    return when {
        recipes.isEmpty() -> recipe
        nextIndex < recipes.size -> recipes[nextIndex]
        else -> recipes[0]
    }
}

fun getPreviousRecipe(recipe: Recipe): Recipe {
    val recipes = RecipeListFragment.recipes
    val previousIndex = recipes.indexOf(recipe) - 1
    return when {
        recipes.isEmpty() -> recipe
        previousIndex >= 0 -> recipes[previousIndex]
        else -> recipes[recipes.size - 1]
    }
}