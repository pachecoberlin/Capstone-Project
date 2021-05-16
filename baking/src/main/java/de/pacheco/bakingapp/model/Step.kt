package de.pacheco.bakingapp.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Step : Parcelable {
    @JvmField
    @SerializedName("id")
    @Expose
    var id: Int

    @SerializedName("shortDescription")
    @Expose
    var shortDescription: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("videoURL")
    @Expose
    var videoURL: String? = null

    @SerializedName("thumbnailURL")
    @Expose
    var thumbnailURL: String? = null

    constructor(id: Int) {
        this.id = id
    }

    protected constructor(`in`: Parcel) {
        id = `in`.readInt()
        shortDescription = `in`.readString()
        description = `in`.readString()
        videoURL = `in`.readString()
        thumbnailURL = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeInt(id)
        parcel.writeString(shortDescription)
        parcel.writeString(description)
        parcel.writeString(videoURL)
        parcel.writeString(thumbnailURL)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Step
        if (id != other.id) return false
        if (shortDescription != other.shortDescription) return false
        if (description != other.description) return false
        if (videoURL != other.videoURL) return false
        if (thumbnailURL != other.thumbnailURL) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (shortDescription?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (videoURL?.hashCode() ?: 0)
        result = 31 * result + (thumbnailURL?.hashCode() ?: 0)
        return result
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Step> = object : Parcelable.Creator<Step> {
            override fun createFromParcel(`in`: Parcel): Step {
                return Step(`in`)
            }

            override fun newArray(size: Int): Array<Step?> {
                return arrayOfNulls(size)
            }
        }
    }
}