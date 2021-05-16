package de.pacheco.bakingapp.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class Ingredient protected constructor(`in`: Parcel) : Parcelable {
    @SerializedName("quantity")
    @Expose
    var quantity: Double? = null

    @SerializedName("measure")
    @Expose
    var measure: String?

    @SerializedName("ingredient")
    @Expose
    var ingredient: String?
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        if (quantity == null) {
            parcel.writeByte(0.toByte())
        } else {
            parcel.writeByte(1.toByte())
            parcel.writeDouble(quantity!!)
        }
        parcel.writeString(measure)
        parcel.writeString(ingredient)
    }

    override fun toString(): String {
        return String.format(Locale.ENGLISH, "%-7.1f %-7s %s\n\n", quantity, measure,
                ingredient)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Ingredient

        if (quantity != other.quantity) return false
        if (measure != other.measure) return false
        if (ingredient != other.ingredient) return false

        return true
    }

    override fun hashCode(): Int {
        var result = quantity?.hashCode() ?: 0
        result = 31 * result + (measure?.hashCode() ?: 0)
        result = 31 * result + (ingredient?.hashCode() ?: 0)
        return result
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Ingredient> = object : Parcelable.Creator<Ingredient> {
            override fun createFromParcel(`in`: Parcel): Ingredient {
                return Ingredient(`in`)
            }

            override fun newArray(size: Int): Array<Ingredient?> {
                return arrayOfNulls(size)
            }
        }
    }

    init {
        quantity = if (`in`.readByte().toInt() == 0) {
            null
        } else {
            `in`.readDouble()
        }
        measure = `in`.readString()
        ingredient = `in`.readString()
    }
}