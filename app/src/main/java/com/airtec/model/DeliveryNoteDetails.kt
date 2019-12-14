package com.airtec.model


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class DeliveryNoteDetails(
    @SerializedName("AddedBy")
    val addedBy: String?,
    @SerializedName("AddedOn")
    val addedOn: String?,
    @SerializedName("Cust_Account_ID")
    val custAccountID: String?,
    @SerializedName("Cust_Account_Number")
    val custAccountNumber: String?,
    @SerializedName("Cust_Name")
    val custName: String?,
    @SerializedName("Del_Date")
    val delDate: String?,
    @SerializedName("Del_Qty")
    val delQty: String?,
    @SerializedName("Delivery_ID")
    val deliveryID: String?,
    @SerializedName("ID")
    val iD: String?,
    @SerializedName("Item_Code")
    val itemCode: String?,
    @SerializedName("Item_Decription")
    val itemDecription: String?,
    @SerializedName("ModifiedBy")
    val modifiedBy: String?,
    @SerializedName("ModifiedOn")
    val modifiedOn: String?,
    @SerializedName("Trip_Number")
    val tripNumber: String?,
    @SerializedName("UOM")
    val uOM: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(addedBy)
        parcel.writeString(addedOn)
        parcel.writeString(custAccountID)
        parcel.writeString(custAccountNumber)
        parcel.writeString(custName)
        parcel.writeString(delDate)
        parcel.writeString(delQty)
        parcel.writeString(deliveryID)
        parcel.writeString(iD)
        parcel.writeString(itemCode)
        parcel.writeString(itemDecription)
        parcel.writeString(modifiedBy)
        parcel.writeString(modifiedOn)
        parcel.writeString(tripNumber)
        parcel.writeString(uOM)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DeliveryNoteDetails> {
        override fun createFromParcel(parcel: Parcel): DeliveryNoteDetails {
            return DeliveryNoteDetails(parcel)
        }

        override fun newArray(size: Int): Array<DeliveryNoteDetails?> {
            return arrayOfNulls(size)
        }
    }
}