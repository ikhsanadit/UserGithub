package com.bangkit2022.usergithub.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit2022.usergithub.database.FavoriteEntity
import com.bangkit2022.usergithub.databinding.ItemUserFlwBinding
import com.bangkit2022.usergithub.ui.detail.DetailActivity
import com.bumptech.glide.Glide


class FavoriteAdapter : RecyclerView.Adapter<FavoriteAdapter.MyViewHolder>() {
    private val userFavor = ArrayList<FavoriteEntity>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView =
            ItemUserFlwBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(userFavor[position])
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(userFavor[position]) }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setListFavorite(items: List<FavoriteEntity>) {
        userFavor.clear()
        userFavor.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount() = userFavor.size
    class MyViewHolder(private val binding: ItemUserFlwBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(favEntity: FavoriteEntity) {
            with(binding) {
                rxName.text = favEntity.login
                Glide.with(root)
                    .load(favEntity.avatar_url)
                    .circleCrop()
                    .into(binding.circleImageView)
                root.setOnClickListener {
                    val intent = Intent(itemView.context, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.KEY_USER, favEntity)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(favEntity: FavoriteEntity)
    }
}