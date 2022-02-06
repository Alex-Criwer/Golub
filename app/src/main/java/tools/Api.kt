package tools

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Interface of the API
 */

interface Api {
    @GET("{section}/{pageNumber}?json=true")
    suspend fun getImage(@Path("section") section: String,
                 @Path("pageNumber") pageNumber: String): List<ImageInfo>

    @GET("/random?json=true")
    suspend fun getRandomImageInfo(): ImageInfo
}
