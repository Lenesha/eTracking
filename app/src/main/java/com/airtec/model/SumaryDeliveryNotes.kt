package com.airtec.model


import com.google.gson.annotations.SerializedName

data class SumaryDeliveryNotes(
    @SerializedName("Del_Qty")
    val delQty: String = "",
    @SerializedName("Item_Code")
    val itemCode: String = "",
    @SerializedName("Item_Decription")
    val itemDecription: String = "",
    @SerializedName("Trip_Number")
    val tripNumber: String=""
)