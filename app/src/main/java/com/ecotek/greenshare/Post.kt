package com.ecotek.greenshare

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration

data class Post(
    val id: String = "",
    val title: String = "",
    val authorID: String = "",
    val content: String = "",
    val publicationDate: String = "",
    val category: String = "",
    // TODO: like and comments
    val likes: Int = 0,
    val comments: List<Comment> = emptyList()
) {
    companion object {
        fun getPostFromFirestore(postId: String, callback: (Post?) -> Unit): ListenerRegistration {
            val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
            val postRef = firestore.collection("posts").document(postId)

            return postRef.addSnapshotListener { snapshot: DocumentSnapshot?, exception: FirebaseFirestoreException? ->
                if (exception != null) {
                    // Erreur lors de la récupération du document
                    callback(null)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val post: Post? = snapshot.toObject(Post::class.java)
                    callback(post)
                } else {
                    // Document introuvable
                    callback(null)
                }
            }
        }
    }
}

//TODO configure like and comments
data class Comment(
    val id: String,
    val author: String,
    val content: String,
    val date: String
)
