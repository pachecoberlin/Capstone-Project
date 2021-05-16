package de.pacheco.sandwichclub.utils

import android.util.Log
import de.pacheco.sandwichclub.model.Sandwich
import org.json.JSONException
import org.json.JSONObject
import java.util.*

private const val TAG = "JsonUtils.kt"
fun parseSandwichJson(json: String): Sandwich {
    val jsonObject: JSONObject
    val name: JSONObject
    try {
        jsonObject = JSONObject(json)
        name = jsonObject.getJSONObject("name")
    } catch (e: JSONException) {
        Log.e(TAG, "Error while parsing JSON", e)
        return Sandwich()
    }
    val mainName = getString(name, "mainName")
    val alsoKnownAs = getList(name, "alsoKnownAs")
    val placeOfOrigin = getString(jsonObject, "placeOfOrigin")
    val description = getString(jsonObject, "description")
    val image = getString(jsonObject, "image")
    val ingredients = getList(jsonObject, "ingredients")
    return Sandwich(mainName, alsoKnownAs, placeOfOrigin, description, image, ingredients)
}

private fun getList(name: JSONObject, string: String): List<String> {
    val list: MutableList<String> = LinkedList()
    try {
        val jsonArray = name.getJSONArray(string)
        for (i in 0 until jsonArray.length()) {
            list.add(jsonArray.getString(i))
        }
    } catch (e: JSONException) {
        Log.e(TAG, "Error while no $string in JSON", e)
    }
    return list
}

private fun getString(jsonObject: JSONObject, string: String): String {
    return try {
        jsonObject.getString(string)
    } catch (e: JSONException) {
        Log.e(TAG, "Error while no $string in JSON", e)
        ""
    }
}
