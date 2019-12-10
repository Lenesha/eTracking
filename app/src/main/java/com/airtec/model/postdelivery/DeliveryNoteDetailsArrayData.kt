package com.airtec.model.postdelivery


import com.google.gson.annotations.SerializedName

data class DeliveryNoteDetailsArrayData(
    @SerializedName("AddedBy")
    val addedBy: String?,
    @SerializedName("AddedOn")
    val addedOn: String?,
    @SerializedName("Cust_Account_ID")
    val custAccountID: Int? = null,
    @SerializedName("Cust_Account_Number")
    val custAccountNumber: String?,
    @SerializedName("Cust_Name")
    val custName: String?,
    @SerializedName("Del_Date")
    val delDate: String?,
    @SerializedName("Del_Qty")
    val delQty: Double? = null,
    @SerializedName("Delivery_ID")
    val deliveryID: Int? = null,
    @SerializedName("Delivery_Note_ID")
    val deliveryNoteID: Int? = null,
    @SerializedName("ID")
    val iD: Int? = null,
    @SerializedName("Item_Code")
    val itemCode: String?,
    @SerializedName("Item_Decription")
    val itemDecription: String?,
    @SerializedName("ModifiedBy")
    val modifiedBy: String?,
    @SerializedName("ModifiedOn")
    val modifiedOn: String?,
    @SerializedName("Status")
    val status: String?,
    @SerializedName("Trip_Number")
    val tripNumber: Int? = null,
    @SerializedName("UOM")
    val uOM: String?
)