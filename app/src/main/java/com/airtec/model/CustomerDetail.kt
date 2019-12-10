package com.airtec.model


import com.google.gson.annotations.SerializedName

data class CustomerDetail(
    @SerializedName("Cust_Account_ID")
    val custAccountID: String = "",
    @SerializedName("Cust_Account_Number")
    val custAccountNumber: String = "",
    @SerializedName("Cust_Name")
    val custName: String = "",
    @SerializedName("Del_Qty")
    val delQty: String = "",
    @SerializedName("Item_Code")
    val itemCode: String = "",
    @SerializedName("Item_Decription")
    val itemDecription: String = "",
    @SerializedName("Trip_Number")
    val tripNumber: String = ""
)