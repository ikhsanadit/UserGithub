package com.bangkit2022.usergithub.repo

import android.app.Application
import androidx.lifecycle.LiveData
import com.bangkit2022.usergithub.database.FavoriteDao
import com.bangkit2022.usergithub.database.FavoriteEntity
import com.bangkit2022.usergithub.database.FavoriteRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FavoriteRepository(application: Application) {
    private val favoritDao: FavoriteDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = FavoriteRoomDatabase.getDatabase(application)
        favoritDao = db.favDao()
    }

    fun getAllFavorites(): LiveData<List<FavoriteEntity>> = favoritDao.getAllFavorite()
    fun getUserFavoriteById(id: Int): LiveData<List<FavoriteEntity>> =
        favoritDao.getUserFavoriteById(id)

    fun insert(fav: FavoriteEntity) {
        executorService.execute { favoritDao.insert(fav) }
    }

    fun delete(fav: FavoriteEntity) {
        executorService.execute { favoritDao.delete(fav) }
    }
}
