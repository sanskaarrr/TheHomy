package com.example.thehomyapp.firebase

import android.app.Activity
import com.example.thehomyapp.activities.DasboardActivity
import com.example.thehomyapp.activities.LoginActivity
import com.example.thehomyapp.activities.ProfileActivity
import com.example.thehomyapp.activities.VerificationActivity
import com.example.thehomyapp.model.User
import com.example.thehomyapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject

class FirestoreClass {
    private val mFireStore= FirebaseFirestore.getInstance()
    fun registerUser(activity: VerificationActivity,userInfo: User){
        val userId=getCurrentUserId()

        mFireStore.collection(Constants.USERS)
            .document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // User already exists, do not register again
                    activity.userAlreadyRegistered()
                } else {
                    // User does not exist, proceed with registration
                    mFireStore.collection(Constants.USERS)
                        .document(userId)
                        .set(userInfo, SetOptions.merge())
                        .addOnSuccessListener {
                            activity.userRegisteredSuccess()
                        }
                }
            }
            .addOnFailureListener { exception ->
            }

    }

    fun loadUserData(activity: Activity){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { doc->
                val loggedInUser= doc.toObject(User::class.java)!!

                when(activity){
                    is DasboardActivity->{
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                    is ProfileActivity->{
                        activity.setUserDataInUI(loggedInUser)
                    }
                }

            }
    }

    fun getCurrentUserId(): String{
        var currentUser= FirebaseAuth.getInstance().currentUser
        var currentUserId=""
        if(currentUser!=null){
            currentUserId=currentUser.uid
        }
        return currentUserId
    }
}