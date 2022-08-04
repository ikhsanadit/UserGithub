package com.bangkit2022.usergithub.ui.detail

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.activity.viewModels
import androidx.annotation.StringRes
import com.bangkit2022.usergithub.R
import com.bangkit2022.usergithub.adapter.SectionPagerAdapter
import com.bangkit2022.usergithub.database.FavoriteEntity
import com.bangkit2022.usergithub.databinding.FragmentDetailUserBinding
import com.bangkit2022.usergithub.datasource.UserResponse
import com.bangkit2022.usergithub.network.ConnecNet
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayoutMediator

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: FragmentDetailUserBinding
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.detailDataLayout.visibility = View.GONE
        val username = intent.getStringExtra(KEY_USERNAME)
        username?.let{
             checkInternetConnection(it) }
    }

    private fun checkInternetConnection(username : String) {
        val user = intent.getParcelableExtra<UserResponse>(KEY_USER)
        val connecNet = ConnecNet(applicationContext)
        connecNet.observe(this, { isConnected ->
            if (isConnected) {
                showNoInternetAnimation(false)
                val favorite = FavoriteEntity()
                favorite.login = username
                favorite.id = intent.getIntExtra(KEY_ID, 0)
                favorite.avatar_url = user?.avatarUrl
                val detailViewModel: DetailViewModel by viewModels {
                    DetailViewModelFactory(username, application)
                }
                detailViewModel.isLoading.observe(this@DetailActivity, {
                    showProgressBar(it)
                })
                detailViewModel.isDataFailed.observe(this@DetailActivity, {
                    showFailedLoadData(it)
                })
                detailViewModel.detailUser.observe(this@DetailActivity, { userResponse ->
                    if (userResponse != null) {
                        setData(userResponse)
                        setTabLayoutAdapter(userResponse)
                    }
                })
                detailViewModel.getFavoriteById(favorite.id!!)
                    .observe(this@DetailActivity, { listFav ->
                        isFavorite = listFav.isNotEmpty()

                        binding.detailFabFavorite.imageTintList = if (listFav.isEmpty()) {
                            ColorStateList.valueOf(Color.rgb(255, 255, 255))
                        } else {
                            ColorStateList.valueOf(Color.rgb(247, 106, 123))
                        }

                    })

                binding.detailFabFavorite.apply {
                    setOnClickListener {
                        if (isFavorite) {
                            detailViewModel.delete(favorite)
                            makeText(
                                this@DetailActivity,
                                "${favorite.login} telah dihapus dari data User Favorite ",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            detailViewModel.insert(favorite)
                            makeText(
                                this@DetailActivity,
                                "${favorite.login} telah ditambahkan ke data User Favorite",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

            } else {
                binding.detailDataLayout.visibility = View.GONE

                showNoInternetAnimation(true)
            }
        })
    }

    private fun setTabLayoutAdapter(user: UserResponse) {
        val sectionPagerAdapter = SectionPagerAdapter(this@DetailActivity)
        sectionPagerAdapter.model = user
        binding.detailViewPager.adapter = sectionPagerAdapter
        TabLayoutMediator(binding.detailTabs, binding.detailViewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        supportActionBar?.elevation = 0f

    }

    private fun setData(userResponse: UserResponse?) {
        if (userResponse != null) {
            with(binding) {
                detailDataLayout.visibility = View.VISIBLE
                civAvatarReceived.visibility = View.VISIBLE
                Glide.with(root)
                    .load(userResponse.avatarUrl)
                    .apply(
                        RequestOptions.placeholderOf(R.drawable.ic_loading)
                            .error(R.drawable.ic_error)
                    )
                    .circleCrop()
                    .into(binding.civAvatarReceived)
                rvName.visibility = View.VISIBLE
                rvUsername.visibility = View.VISIBLE
                rvName.text = userResponse.name
                rvUsername.text = userResponse.login
                if (userResponse.bio != null) {
                    detailBio.visibility = View.VISIBLE
                    detailBio.text = userResponse.bio
                } else {
                    detailBio.visibility = View.GONE
                }
                if (userResponse.company != null) {
                    detailCompany.visibility = View.VISIBLE
                    detailCompany.text = userResponse.company
                } else {
                    detailCompany.visibility = View.GONE
                }
                if (userResponse.location != null) {
                    detailLocation.visibility = View.VISIBLE
                    detailLocation.text = userResponse.location
                } else {
                    detailLocation.visibility = View.GONE
                }

                if (userResponse.followers != null) {
                    detailFollowersValue.visibility = View.VISIBLE
                    detailFollowersValue.text = userResponse.followers
                } else {
                    detailFollowersValue.visibility = View.GONE
                }
                if (userResponse.followers != null) {
                    rxFollowers.visibility = View.VISIBLE
                } else {
                    rxFollowers.visibility = View.GONE
                }
                if (userResponse.following != null) {
                    detailFollowingValue.visibility = View.VISIBLE
                    detailFollowingValue.text = userResponse.following
                } else {
                    detailFollowingValue.visibility = View.GONE
                }
                if (userResponse.following != null) {
                    rxFollowing.visibility = View.VISIBLE
                } else {
                    rxFollowing.visibility = View.GONE
                }

            }
        } else {
            Log.i(TAG, "setData function is error")
        }
    }

    private fun showProgressBar(isLoading: Boolean) {
        binding.detailAnimLoader.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showNoInternetAnimation(isNoInternet: Boolean) {
        binding.detailNoInternet.visibility = if (isNoInternet) View.VISIBLE else View.GONE
    }

    private fun showFailedLoadData(isFailed: Boolean) {
        binding.detailFailedDataLoad.visibility = if (isFailed) View.VISIBLE else View.GONE
        binding.tvFailed.visibility = if (isFailed) View.VISIBLE else View.GONE

    }

    companion object {
        const val KEY_USER = "user"
        private const val TAG = "DetailActivity"
        const val KEY_USERNAME = "username"
        const val KEY_ID = "extra id"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_flwr,
            R.string.tab_text_flwg
        )
    }

}