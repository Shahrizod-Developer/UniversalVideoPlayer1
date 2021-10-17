package uz.android.universalvideoplayer.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiUtilities {

    companion object {

        var retrofit: Retrofit? = null
        private val BASE_URL = "https://instagram-unofficial-api.herokuapp.com/unofficial/api/"

        fun getApiInterface(): ApiInterface? {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .build()
            }
            return retrofit!!.create(ApiInterface::class.java)
        }
    }
}