package com.bangkit2022.usergithub.network

import com.bangkit2022.usergithub.BuildConfig
import com.bangkit2022.usergithub.datasource.SearchResponse
import com.bangkit2022.usergithub.datasource.UserResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("users")
    @Headers("Authorization: token $API_TOKEN", "UserResponse-Agent: request")
    suspend fun getUserListAsync(): ArrayList<UserResponse>

    @GET("users/{username}")
    @Headers("Authorization: token $API_TOKEN", "UserResponse-Agent: request")
    suspend fun getDetailUserAsync(@Path("username") username: String): UserResponse

    @GET("search/users")
    @Headers("Authorization: token $API_TOKEN", "UserResponse-Agent: request")
    fun getUserBySearch(@Query("q") username: String): Call<SearchResponse>

    @GET("users/{username}/followers")
    @Headers("Authorization: token $API_TOKEN", "UserResponse-Agent: request")
    suspend fun getFollowersList(@Path("username") username: String): ArrayList<UserResponse>

    @GET("users/{username}/following")
    @Headers("Authorization: token $API_TOKEN", "UserResponse-Agent: request")
    suspend fun getFollowingList(@Path("username") username: String): ArrayList<UserResponse>

    companion object {
        private const val API_TOKEN = BuildConfig.API_TOKEN
    }
}