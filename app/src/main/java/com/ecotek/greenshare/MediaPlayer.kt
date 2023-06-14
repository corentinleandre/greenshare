package com.ecotek.greenshare

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.VideoView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MediaPlayer(uri: Uri) : Fragment() {
    val uri2 = uri
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.mediaplayer_fragment, container, false)
        val vidLayout = view.findViewById<VideoView>(R.id.idVideoView)
        vidLayout.setVideoURI(uri2)
        vidLayout.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.setVolume(0f, 0f)
            mediaPlayer.isLooping = true
            mediaPlayer.start()
        }
        return view
    }
}