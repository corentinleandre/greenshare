package com.ecotek.greenshare

/**
 * Data class representing media information.
 *
 * @property id     The unique identifier of the media.
 * @property media1 The first media file.
 * @property media2 The second media file.
 * @property media3 The third media file.
 * @property media4 The fourth media file.
 */
data class Media(
    val id: String,
    val type: String,
    val media1: String,
    val media2: String,
    val media3: String,
    val media4: String
) {
}
