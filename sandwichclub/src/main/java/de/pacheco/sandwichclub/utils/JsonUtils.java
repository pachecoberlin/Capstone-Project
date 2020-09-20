package de.pacheco.sandwichclub.utils;

import android.util.Log;

import de.pacheco.sandwichclub.model.Sandwich;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class JsonUtils {

    public static Sandwich parseSandwichJson(String json) {
        JSONObject jsonObject;
        JSONObject name;
        try {
            jsonObject = new JSONObject(json);
            name = jsonObject.getJSONObject("name");
        } catch (JSONException e) {
            Log.e(JsonUtils.class.toString(), "Error while parsing JSON", e);
            return null;
        }
        String mainName = getString(name, "mainName");
        List<String> alsoKnownAs = getList(name, "alsoKnownAs");
        String placeOfOrigin = getString(jsonObject, "placeOfOrigin");
        String description = getString(jsonObject, "description");
        String image = getString(jsonObject, "image");
        List<String> ingredients = getList(jsonObject, "ingredients");
        return new Sandwich(mainName, alsoKnownAs, placeOfOrigin, description, image, ingredients);
    }

    private static List<String> getList(JSONObject name, String string) {
        List<String> list = new LinkedList<>();
        try {
            JSONArray jsonArray = name.getJSONArray(string);
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            Log.e(JsonUtils.class.toString(), "Error while no " + string + " in JSON", e);
        }
        return list;
    }

    private static String getString(JSONObject jsonObject, String string) {
        try {
            return jsonObject.getString(string);
        } catch (JSONException e) {
            Log.e(JsonUtils.class.toString(), "Error while no " + string + " in JSON", e);
            return "";
        }
    }
}