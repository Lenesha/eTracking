package com.airtec.model


import com.google.gson.annotations.SerializedName

data class TripDetail(
    @SerializedName("AddedBy")
    val addedBy: String,
    @SerializedName("AddedOn")
    val addedOn: String,
    @SerializedName("Driver_ID")
    val driverID: String,
    @SerializedName("Driver_Name")
    val driverName: String,
    @SerializedName("ModifiedBy")
    val modifiedBy: String,
    @SerializedName("ModifiedOn")
    val modifiedOn: String,
    @SerializedName("TID")
    val tID: String,
    @SerializedName("Trip_Date")
    val tripDate: String,
    @SerializedName("Trip_Number")
    val tripNumber: String,
    @SerializedName("Trip_Status")
    val tripStatus: String,
    @SerializedName("Trip_Status1")
    val tripStatus1: String,
    @SerializedName("Trip_Status_ID")
    val tripStatusID: String,
    @SerializedName("Veh_ID")
    val vehID: String,
    @SerializedName("Veh_Number")
    val vehNumber: String
)