package com.example.thehomyapp.firebase

import com.example.thehomyapp.activities.LoginActivity
import com.example.thehomyapp.activities.VerificationActivity
import com.example.thehomyapp.model.User
import com.example.thehomyapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

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

    fun getCurrentUserId(): String{
        var currentUser= FirebaseAuth.getInstance().currentUser
        var currentUserId=""
        if(currentUser!=null){
            currentUserId=currentUser.uid
        }
        return currentUserId
    }
}