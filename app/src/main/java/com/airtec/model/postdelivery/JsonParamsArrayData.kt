package com.airtec.model.postdelivery


import com.google.gson.annotations.SerializedName

data class JsonParamsArrayData(
    @SerializedName("Trip_Number")
    val tripNumber: String = ""
)