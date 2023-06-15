package com.ecotek.greenshare

data class User(val identification: String ="",
                val firstname: String = "",
                val lastname: String = "",
                val telephone: String,
                val email: String = "",
                val role: String = "",
                val group: String,
                val icon: String,
                var rights: String
){
    fun setModerator(){
        rights="1"
    }
    fun setAdmin(){
        rights="2"
    }
}