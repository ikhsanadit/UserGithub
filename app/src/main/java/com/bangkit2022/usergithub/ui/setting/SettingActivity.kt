package com.bangkit2022.usergithub.ui.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bangkit2022.usergithub.databinding.FragmentSettingBinding

class SettingActivity : AppCompatActivity() {
    private val dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private lateinit var binding: FragmentSettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val pref = SettingPreferences.getInstance(dataStore)
        val viewModel = ViewModelProvider(
            this,
            SettingViewModelFactory(pref)
        ).get(SettingViewModel::class.java)

        viewModel.getThemeSettings().observe(this,
            { isDarkModeActive: Boolean ->
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    binding.rxSwitch.isChecked = true
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    binding.rxSwitch.isChecked = false
                }
            })

        binding.rxSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.rxSwitch.isChecked = isChecked
            viewModel.saveThemeSetting(isChecked)
        }
    }
}