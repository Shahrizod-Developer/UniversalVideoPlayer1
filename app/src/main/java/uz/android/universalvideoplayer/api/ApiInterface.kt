package uz.android.universalvideoplayer.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import uz.android.universalvideoplayer.models.InstaModel

interface ApiInterface {

    @GET("video")
    fun getInfo(
        @Query("link") link: String?
    ): Call<InstaModel?>?
}