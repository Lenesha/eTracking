package com.airtec.model.postdelivery


import com.google.gson.annotations.SerializedName

data class DeliveryNoteDetailsData(
    @SerializedName("DeliveryNoteDetailsArrayData")
    val deliveryNoteDetailsArrayData: ArrayList<DeliveryNoteDetailsArrayData?> =ArrayList(),
    @SerializedName("EmptyscannedArrayData")
    val emptyscannedArrayData: List<EmptyscannedArrayData> = listOf(),
    @SerializedName("jsonParamsArrayData")
    val jsonParamsArrayData: List<JsonParamsArrayData> = listOf(),
    @SerializedName("scannedArrayData")
    val scannedArrayData: List<ScannedArrayData> = listOf(),
    @SerializedName("signatureArrayData")
    val signatureArrayData: List<SignatureArrayData> = listOf()
)