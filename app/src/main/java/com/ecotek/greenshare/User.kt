package com.ecotek.greenshare

import android.media.Image

data class User(val identification: String ="",
           val firstname: String = "",
           val lastname: String = "",
           var telephone: String,
           val email: String = "",
           val role: String = "",
           var group: String,
           var icon: String,
           var rights: String
){
    fun setModerator(){
        rights="1"
    }
    fun setAdmin(){
        rights="2"
    }
}