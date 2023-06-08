package com.ecotek.greenshare

import android.media.Image

class User(val identification: String = "",
           val firstname: String = "",
           val lastname: String = "",
           val telephone: Int,
           val email: String = "",
           val role: Boolean = false,
           val group: List<String>,
           val icon: Image,
           val rights: Short = 0
           )