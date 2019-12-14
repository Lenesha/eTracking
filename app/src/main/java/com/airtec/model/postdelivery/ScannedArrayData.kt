package com.airtec.model.postdelivery


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ScannedArrayData(
    @SerializedName("AddedBy")
    val addedBy: String? = "",
    @SerializedName("AddedOn")
    val addedOn: String? = "",
    @SerializedName("BarcodeValue")
    val barcodeValue: String? = "",
    @SerializedName("ModifiedBy")
    val modifiedBy: String? = "",
    @SerializedName("ModifiedOn")
    val modifiedOn: String? = "",
    @SerializedName("SlNo")
    val slNo:Int? = 1,
    @SerializedName("TripNumber")
    val tripNumber: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(addedBy)
        parcel.writeString(addedOn)
        parcel.writeString(barcodeValue)
        parcel.writeString(modifiedBy)
        parcel.writeString(modifiedOn)
        parcel.writeValue(slNo)
        parcel.writeString(tripNumber)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScannedArrayData> {
        override fun createFromParcel(parcel: Parcel): ScannedArrayData {
            return ScannedArrayData(parcel)
        }

        override fun newArray(size: Int): Array<ScannedArrayData?> {
            return arrayOfNulls(size)
        }
    }
}