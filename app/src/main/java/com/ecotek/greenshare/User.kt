package com.ecotek.greenshare

import android.media.Image

/**
 * Data class representing a User.
 *
 * @property identification The identification of the user.
 * @property firstname      The first name of the user.
 * @property lastname       The last name of the user.
 * @property telephone      The telephone number of the user.
 * @property email          The email address of the user.
 * @property role           The role of the user.
 * @property group          The group(s) the user belongs to.
 * @property icon           The icon associated with the user.
 * @property rights         The rights assigned to the user.
 */
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