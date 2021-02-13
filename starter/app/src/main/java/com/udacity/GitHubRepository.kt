package com.udacity

import android.content.Context

enum class GitHubRepository(val title: String, val url: String) {
    GLIDE(
        "Glide",
        "https://github.com/bumptech/glide"
    ),
    RETROFIT(
        "Retrofit",
        "https://github.com/square/retrofit"
    ),
    UDACITY(
        "Udacity",
        "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
    );

    fun getDescription(context: Context): String = context.getString(
        when (this) {
            GLIDE -> R.string.glide_download
            UDACITY -> R.string.udacity_download
            RETROFIT -> R.string.retrofit_download
        }
    )
}