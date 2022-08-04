package com.bangkit2022.usergithub.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit2022.usergithub.R
import com.bangkit2022.usergithub.databinding.ItemUserFlwBinding
import com.bangkit2022.usergithub.datasource.UserResponse
import com.bangkit2022.usergithub.ui.detail.DetailActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class FollowsAdapter :
    RecyclerView.Adapter<FollowsAdapter.MyViewHolder>() {
    private var listUserResponse = ArrayList<UserResponse>()

    @SuppressLint("NotifyDataSetChanged")
    fun addDataToList(items: ArrayList<UserResponse>) {
        listUserResponse.clear()
        listUserResponse.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemUserFlwBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(listUserResponse[position])
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(listUserResponse[position]) }

    }

    override fun getItemCount() = listUserResponse.size

    class MyViewHolder(private var binding: ItemUserFlwBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(userResponse: UserResponse) {
            binding.rxName.text = userResponse.login
            Glide.with(binding.root)
                .load(userResponse.avatarUrl)
                .apply(
                    RequestOptions.placeholderOf(R.drawable.ic_loading)
                        .error(R.drawable.ic_error)
                )
                .circleCrop()
                .into(binding.circleImageView)
            binding.root.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.KEY_USER, userResponse)
                itemView.context.startActivity(intent)
            }
        }
    }

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(user: UserResponse)
    }
}
