package com.airtec.model


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
)