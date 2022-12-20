package com.example.android.politicalpreparedness.election.adapter

import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.election.WebViewActivity
import com.example.android.politicalpreparedness.network.models.Election

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Election>?) {
    val adapter = recyclerView.adapter as ElectionListAdapter
    adapter.submitList(data)
}

@BindingAdapter("clickableText")
fun bindClickableText(textView: TextView, textUrl: String?) {
    Log.i("BindingAdapter-clickableText",
        "TextView:${textView.id} AND textURL:${textUrl}")
    if (textUrl.isNullOrEmpty())
        return
   // val spannableString = SpannableString(textUrl)
//    val clickableSpan: ClickableSpan = object : ClickableSpan() {
//        override fun onClick(widget: View) {
//            val intent = Intent(textView.context, WebViewActivity::class.java)
//            intent.putExtra("URL",textUrl)
//            textView.context.startActivity(intent)
//        }
//    }
    textView.setOnClickListener(View.OnClickListener {
        val intent = Intent(textView.context, WebViewActivity::class.java)
        intent.putExtra("URL",textUrl)
        textView.context.startActivity(intent)
    })
 //   spannableString.setSpan(clickableSpan, 0, textUrl.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
  //textView.setText(spannableString, TextView.BufferType.SPANNABLE)
  //  textView.movementMethod = LinkMovementMethod.getInstance()
}