package de.pacheco.sandwichclub.model

data class Sandwich(
        var mainName: String = "",
        var alsoKnownAs: List<String> = mutableListOf(),
        var placeOfOrigin: String = "",
        var description: String = "",
        var image: String = "",
        var ingredients: List<String> = mutableListOf(),
)