package com.example.android.politicalpreparedness.representative.adapter

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.RepresentativeListBinding
import com.example.android.politicalpreparedness.network.models.Channel
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.util.Constants.FACEBOOK_CHANNEL
import com.example.android.politicalpreparedness.util.Constants.FACEBOOK_URL
import com.example.android.politicalpreparedness.util.Constants.TWITTER_CHANNEL
import com.example.android.politicalpreparedness.util.Constants.TWITTER_URL

class RepresentativeListAdapter: ListAdapter<Representative, RepresentativeListAdapter.RepresentativeViewHolder>( RepresentativeDiffCallback)
{
    // Add companion object to inflate ViewHolder (from)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepresentativeViewHolder {
        return RepresentativeViewHolder(RepresentativeListBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RepresentativeViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


class RepresentativeViewHolder(val binding: RepresentativeListBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Representative) {
        binding.representative = item

        showSocialLinks(item.official.channels)
        showWWWLinks(item.official.urls)

        binding.executePendingBindings()
    }



    private fun showSocialLinks(channels: List<Channel>?) {
        val facebookUrl = getFacebookUrl(channels)
        if (!facebookUrl.isNullOrBlank()) {
            enableLink(binding.imageViewFacebook, facebookUrl)
        }
        else {
            binding.imageViewFacebook.visibility = View.GONE
        }

        val twitterUrl = getTwitterUrl(channels)
        if (!twitterUrl.isNullOrBlank()) {
            enableLink(binding.imageViewTwitter, twitterUrl)
        }
        else {
            binding.imageViewTwitter.visibility = View.GONE
        }
    }

    private fun showWWWLinks(urls: List<String>?) {
        if (urls != null) {
            enableLink(binding.imageViewWeb, urls.first())
        }
        else {
            binding.imageViewWeb.visibility = View.GONE
        }
    }

    private fun getFacebookUrl(channels: List<Channel>?): String? {
        return channels?.filter { channel -> channel.type == FACEBOOK_CHANNEL }
            ?.map { channel -> FACEBOOK_URL.plus("${channel.id}") }
            ?.firstOrNull()
    }

    private fun getTwitterUrl(channels: List<Channel>?): String? {
        return channels?.filter { channel -> channel.type == TWITTER_CHANNEL }
            ?.map { channel -> TWITTER_URL.plus("${channel.id}") }
            ?.firstOrNull()
    }

    private fun enableLink(view: ImageView, url: String) {
        view.visibility = View.VISIBLE
        view.setOnClickListener { setIntent(url) }
    }

    private fun setIntent(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(ACTION_VIEW, uri)
        itemView.context.startActivity(intent)
    }
}


    companion object RepresentativeDiffCallback : DiffUtil.ItemCallback<Representative>() {
        override fun areItemsTheSame(oldItem: Representative, newItem: Representative): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Representative, newItem: Representative): Boolean {
            return oldItem.official.name == newItem.official.name
        }
    }
}
