package com.example.android.politicalpreparedness.election.adapter

import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.election.WebViewActivity
import com.example.android.politicalpreparedness.network.ApiStatus
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.util.Constants.WEB_VIEW_URL
import timber.log.Timber

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Election>?) {
    val adapter = recyclerView.adapter as ElectionListAdapter?
    adapter?.submitList(data)
}

@BindingAdapter("clickableText")
fun bindClickableText(textView: TextView, textUrl: String?) {
    Timber.i("BindingAdapter-clickableText"+
        "TextView:${textView.id} AND textURL:${textUrl}")
    if (textUrl.isNullOrEmpty())
        return
    textView.setOnClickListener(View.OnClickListener {
        val intent = Intent(textView.context, WebViewActivity::class.java)
        intent.putExtra(WEB_VIEW_URL,textUrl)
        textView.context.startActivity(intent)
    })
}

@BindingAdapter("toggleButtonText")
fun bindFollowButtonText(button: Button, isElectionSaved: Boolean?) {
    if(isElectionSaved != null) {
        if (isElectionSaved) {
            button.text = button.resources.getString(R.string.unfollow_election)
        } else {
            button.text = button.resources.getString(R.string.follow_election)
        }
    } else {
        button.text = ""
    }
}

@BindingAdapter("apiStatus")
fun bindApiStatus(progressBar: ProgressBar, statusApi: ApiStatus?) {
    when (statusApi) {
        ApiStatus.LOADING -> {
            progressBar.visibility = View.VISIBLE
        }
        else -> {
            progressBar.visibility = View.GONE
        }
    }
}

