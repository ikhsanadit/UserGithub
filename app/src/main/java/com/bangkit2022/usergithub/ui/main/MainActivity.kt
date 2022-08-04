package com.bangkit2022.usergithub.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bangkit2022.usergithub.R
import com.bangkit2022.usergithub.adapter.OnItemClickCallback
import com.bangkit2022.usergithub.adapter.UserAdapter
import com.bangkit2022.usergithub.databinding.ActivityMainBinding
import com.bangkit2022.usergithub.datasource.UserResponse
import com.bangkit2022.usergithub.network.ConnecNet
import com.bangkit2022.usergithub.repo.UserRepository
import com.bangkit2022.usergithub.ui.detail.DetailActivity
import com.bangkit2022.usergithub.ui.favorite.FavoriteActivity
import com.bangkit2022.usergithub.ui.setting.SettingActivity
import com.bangkit2022.usergithub.ui.setting.SettingPreferences


class MainActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private lateinit var binding: ActivityMainBinding

    private val adapter: UserAdapter by lazy {
        UserAdapter()
    }
    private lateinit var mainViewMod: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpToolbar()
        setViewModel()
        ProgressBar()
        darkModeCheck()
        checkInternetConnection()
        setUpSearchView()
    }
    private fun setViewModel(){
        val pref = SettingPreferences.getInstance(dataStore)
        mainViewMod = ViewModelProvider(this, MainViewModelFactory(pref))[MainViewModel::class.java]
    }
    private fun darkModeCheck(){
        mainViewMod.getThemeSettings().observe(this@MainActivity,{isDarkModeActive ->
            if (isDarkModeActive) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        })

    }

    private fun setUpToolbar() {
        binding.rdToolbar.setOnMenuItemClickListener(this)
    }

    private fun setUpSearchView() {

        with(binding) {
            rdSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    showFailedLoadData(false)
                    showProgressBar(true)
                    UserRepository.getUserBySearch(query)
                    mainViewMod.searchUser.observe(this@MainActivity) { searchUserResponse ->
                        if (searchUserResponse != null) {
                            adapter.addDataToList(searchUserResponse)
                            rdSearchView.clearFocus()
                        }
                    }
                    return true
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    return false
                }
            })

        }
    }

    private fun ProgressBar() {
        mainViewMod.isLoading.observe(this, {
            showProgressBar(it)
        })
        mainViewMod.isDataFailed.observe(this, {
            showFailedLoadData(it)
        })
    }

    private fun checkInternetConnection() {
        val connecNet = ConnecNet(applicationContext)
        connecNet.observe(this, { isConnected ->
            if (isConnected) {
                showFailedLoadData(false)
                mainViewMod.user.observe(this, { userResponse ->
                    if (userResponse != null) {
                        adapter.addDataToList(userResponse)
                        setUserData()
                    }
                })
                mainViewMod.searchUser.observe(this@MainActivity) { searchUserResponse ->
                    if (searchUserResponse != null) {
                        adapter.addDataToList(searchUserResponse)
                        binding.rdMain.visibility = View.VISIBLE
                    }
                }
            } else {
                mainViewMod.user.observe(this, { userResponse ->
                    if (userResponse != null) {
                        adapter.addDataToList(userResponse)
                        setUserData()
                    }
                })
                makeText(this@MainActivity, "Tidak ada koneksi internet", Toast.LENGTH_LONG).show()
            }
        })

    }

    private fun hideUserList() {
        binding.rdMain.layoutManager = null
        binding.rdMain.adapter = null
    }

    private fun showProgressBar(isLoading: Boolean) {
        binding.rdAnimLoader.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    @Suppress("SameParameterValue")
    private fun showFailedLoadData(isFailed: Boolean) {
        binding.animFailedDataLoad.visibility = if (isFailed) View.VISIBLE else View.GONE
        binding.rdFailed.visibility = if (isFailed) View.VISIBLE else View.GONE

    }

    private fun setUserData() {
        with(binding) {
            val layoutManager =
                GridLayoutManager(this@MainActivity, 1, GridLayoutManager.VERTICAL, false)
            rdMain.layoutManager = layoutManager
            rdMain.adapter = adapter
            adapter.setOnItemClickCallback(object : OnItemClickCallback {
                override fun onItemClicked(user: UserResponse) {
                    hideUserList()
                    val intent = Intent(this@MainActivity, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.KEY_USER, user)
                    intent.putExtra(DetailActivity.KEY_USERNAME, user.login)
                    intent.putExtra(DetailActivity.KEY_ID, user.id)
                    startActivity(intent)
                }
            })
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.btn_setting -> {
                val setting = Intent(this, SettingActivity::class.java)
                startActivity(setting)
                true
            }
            R.id.btn_favorite -> {
                val favorite = Intent(this, FavoriteActivity::class.java)
                startActivity(favorite)
                true
            }
            else -> false
        }
    }


}
