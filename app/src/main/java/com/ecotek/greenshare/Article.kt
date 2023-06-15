package com.ecotek.greenshare

import com.google.firebase.firestore.FirebaseFirestore

/**
 * Represents an article.
 *
 * @property id The ID of the article.
 * @property title The title of the article.
 * @property authorID The ID of the author.
 * @property content The content of the article.
 * @property date The date of the article.
 * @property mediasID The ID of the associated media.
 * @property commentID The ID of the comments section.
 */
data class Article(
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
        /**
         * The function "getArticle" retrieves an article from the Firestore database.
         *
         * @param id The ID of the article to retrieve.
         * @param callback The callback function to be called with the retrieved article.
         */
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

