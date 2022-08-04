package com.bangkit2022.usergithub.ui.favorite

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bangkit2022.usergithub.database.FavoriteEntity
import com.bangkit2022.usergithub.repo.FavoriteRepository

class FavoriteViewModel(application : Application) : ViewModel() {
    private val mFavorRepo : FavoriteRepository = FavoriteRepository(application)
    fun getAllFavorites() : LiveData<List<FavoriteEntity>> = mFavorRepo.getAllFavorites()
}