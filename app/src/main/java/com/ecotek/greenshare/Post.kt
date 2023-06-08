package com.ecotek.greenshare

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener

data class Post(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val content: String = "",
    val publicationDate: String = "",
    val category: String = "",
    // TODO: like and comments
    val likes: Int,
    val comments: List<Comment>
) {
    companion object {
        fun getPostFromFirebase(articleId: String, callback: (Post?) -> Unit) {
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val articleRef: DatabaseReference = database.getReference("articles").child(articleId)

            articleRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val article: Post? = dataSnapshot.getValue(Post::class.java)
                    callback(article)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    callback(null)
                }
            })
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
