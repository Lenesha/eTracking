package com.airtec.model.postdelivery


import com.google.gson.annotations.SerializedName

data class EmptyscannedArrayData(
    @SerializedName("AddedBy")
    val addedBy: String? = "",
    @SerializedName("AddedOn")
    val addedOn: String?= "",
    @SerializedName("BarcodeValue")
    val barcodeValue: String? = "",
    @SerializedName("ModifiedBy")
    val modifiedBy: String? = "",
    @SerializedName("ModifiedOn")
    val modifiedOn: String? = "",
    @SerializedName("SlNo")
    val slNo: Int? = 0,
    @SerializedName("TripNumber")
    val tripNumber: String? = ""
)