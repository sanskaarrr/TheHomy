package com.example.thehomyapp.model

import android.os.Parcel
import android.os.Parcelable

data class User (
    val id:String="",

    val phoneNumber: String="",
    val name:String="",
    val email: String="",
    val image:String="",
    val fcmToken:String="",
    val current_address:String="",
        ):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(phoneNumber)
        parcel.writeString(email)
        parcel.writeString(image)
        parcel.writeString(fcmToken)
        parcel.writeString(current_address)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
