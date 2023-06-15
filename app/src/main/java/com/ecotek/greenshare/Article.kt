package com.ecotek.greenshare

import com.google.firebase.firestore.FirebaseFirestore

data class   Article(
    val id: String = "",
    val title: String = "",
    val authorID: String = "",
    val content: String = "",
    val date: String = "",
    var group: String = "",
    var likes: String = "",
    val mediasID: String = "",
    var commentID: String = "",
    var verified: String = ""
    ){

    //when moderator validate article
    fun validateModo(user: User, id:String){
        val mFirestore = FirebaseFirestore.getInstance()

        mFirestore.collection("Article")
            .document(id)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (user.rights!="0"){
                    verified="1"
                }
            }
    }


    companion object {
        fun getArticle(id: String, callback: (Article?) -> Unit) {
            val mFirestore = FirebaseFirestore.getInstance()

            mFirestore.collection("Article")
                .document(id)
                .get()
                .addOnSuccessListener { document ->
                    val article = document.toObject(Article::class.java)
                    callback(article)
                }
                .addOnFailureListener { exception ->
                    callback(null)
                }
        }
    }
}

