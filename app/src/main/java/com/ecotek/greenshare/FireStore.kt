package com.ecotek.greenshare

import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FireStore {

    private val mFirestore = FirebaseFirestore.getInstance()

    fun senddata(userInfo: User){
        mFirestore.collection("Users")
            .document(userInfo.identification)
            .set(userInfo, SetOptions.merge())
        //.addOnSuccessListener { }
    }

    fun getdata() :String{
        var currentUsername = ""
        return currentUsername
    }

    fun getname(activity: FirstFragment,identification : String){

        mFirestore.collection("Users")
            .document(identification)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                if (user != null) {
                    //TODO
                }
            }
    }
}