package com.bangkit2022.usergithub.ui.follow

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit2022.usergithub.datasource.UserResponse
import com.bangkit2022.usergithub.network.ApiConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FollowsViewModel(username: String) : ViewModel() {
    private val xfollowers = MutableLiveData<ArrayList<UserResponse>?>()
    val followers: LiveData<ArrayList<UserResponse>?> = xfollowers
    private val yfollowing = MutableLiveData<ArrayList<UserResponse>?>()
    val following: LiveData<ArrayList<UserResponse>?> = yfollowing
    private val zisLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = zisLoading
    private val aisDataFailed = MutableLiveData<Boolean>()
    val isDataFailed: LiveData<Boolean> = aisDataFailed

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        viewModelScope.launch {
            getFollowersList(username)
            getFollowingList(username)
        }
        Log.i(TAG, "FollFragment is Created")

    }

    private suspend fun getFollowersList(username: String) {
        coroutineScope.launch {
            zisLoading.value = true
            val result = ApiConfig.getApiService().getFollowersList(username)
            try{
                zisLoading.value = false
                xfollowers.postValue(result)
            }catch (e: Exception){
                zisLoading.value = false
                aisDataFailed.value = true
                Log.e(TAG, "OnFailure: ${e.message.toString()}")
            }
        }
    }

    private suspend fun getFollowingList(username: String) {
        coroutineScope.launch {
            zisLoading.value = true
            val result = ApiConfig.getApiService().getFollowingList(username)
            try{
                zisLoading.value = false
                yfollowing.postValue(result)
            }catch (e: Exception){
                zisLoading.value = false
                aisDataFailed.value = true
                Log.e(TAG, "OnFailure: ${e.message.toString()}")
            }
        }
    }
    companion object {
        private const val TAG = "FollowsViewModel"
    }
}