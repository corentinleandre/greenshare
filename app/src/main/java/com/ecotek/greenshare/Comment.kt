package com.ecotek.greenshare

import com.google.firebase.firestore.FirebaseFirestore

data class Comment(
    val authorEmail:String = "",
    val content:String = "",
    val likes:String = ""
) {
    companion object {

        }
}