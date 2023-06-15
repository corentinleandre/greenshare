package com.ecotek.greenshare

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

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
//        fun postArticle(title: String = "",
//                        authorID: String = "",
//                        content: String = "",
//                        date: String = "",
//                        commentID: String = ""){
//            val mFirestore = FirebaseFirestore.getInstance()
//            val collection = mFirestore.collection("Article")
//            collection
//                .get()
//                .addOnSuccessListener { querySnapshot ->
//                    var highestId = 0
//                    for (document in querySnapshot) {
//                        val id = document.id.toIntOrNull()
//                        if (id != null && id > highestId) {
//                            highestId = id
//                        }
//                    }
//                    highestId += 1
//                    val article = Article(highestId.toString(),title,authorID,content,date,commentID)
//
//                    mFirestore.collection("Article")
//                        .document(article.id.toString())
//                        .set(article, SetOptions.merge())
//                    //.addOnSuccessListener { }
//                }
//                .addOnFailureListener { exception ->
//                    //
//                }
//        }
    }
}

