package de.pacheco.bakingapp.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class Recipe : Parcelable {
    @SerializedName("id")
    @Expose
    var id = 0

    @SerializedName("ingredients")
    @Expose
    var ingredients: List<Ingredient> = LinkedList()

    @SerializedName("steps")
    @Expose
    var steps: List<Step>

    @SerializedName("name")
    @Expose
    var name: String = ""

    @SerializedName("servings")
    @Expose
    var servings = 0

    @SerializedName("image")
    @Expose
    var image: String = ""

    constructor(steps: List<Step>) {
        this.steps = steps
    }

    internal constructor(`in`: Parcel) {
        id = `in`.readInt()
        name = `in`.readString().toString()
        servings = `in`.readInt()
        image = `in`.readString().toString()
        ingredients = LinkedList()
        `in`.readList(ingredients, Ingredient::class.java.classLoader)
        steps = LinkedList()
        `in`.readList(steps, Step::class.java.classLoader)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(servings)
        parcel.writeString(image)
        parcel.writeList(ingredients)
        parcel.writeList(steps)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val recipe = other as Recipe
        return id == recipe.id && servings == recipe.servings &&
                ingredients == recipe.ingredients &&
                steps == recipe.steps &&
                name == recipe.name &&
                image == recipe.image
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + ingredients.hashCode()
        result = 31 * result + steps.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + servings
        result = 31 * result + image.hashCode()
        return result
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Recipe> = object : Parcelable.Creator<Recipe> {
            override fun createFromParcel(`in`: Parcel): Recipe {
                return Recipe(`in`)
            }

            override fun newArray(size: Int): Array<Recipe?> {
                return arrayOfNulls(size)
            }
        }
    }
}