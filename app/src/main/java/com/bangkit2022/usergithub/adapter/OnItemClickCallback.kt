package com.bangkit2022.usergithub.adapter

import com.bangkit2022.usergithub.datasource.UserResponse

interface OnItemClickCallback {
    fun onItemClicked(user: UserResponse)
}