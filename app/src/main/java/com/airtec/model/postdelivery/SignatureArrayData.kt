package com.airtec.model.postdelivery


import com.google.gson.annotations.SerializedName

data class SignatureArrayData(
    @SerializedName("AddedBy")
    val addedBy: String? = "",
    @SerializedName("AddedOn")
    val addedOn: String? = "",
    @SerializedName("ModifiedBy")
    val modifiedBy: String? = "",
    @SerializedName("ModifiedOn")
    val modifiedOn: String? = "",
    @SerializedName("Sign")
    val sign: String? = "",
    @SerializedName("TripNumber")
    val tripNumber: String? = ""
)