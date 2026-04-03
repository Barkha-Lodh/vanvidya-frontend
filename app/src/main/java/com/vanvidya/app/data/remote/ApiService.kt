package com.vanvidya.app.data.remote

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("external/complete/")
    suspend fun getCompletePlantInfo(
        @Query("name") plantName: String
    ): Response<CompletePlantInfo>

    @Multipart
    @POST("identify-leaf/")
    suspend fun identifyLeaf(
        @Part image: MultipartBody.Part
    ): Response<LeafIdentificationResponse>
}