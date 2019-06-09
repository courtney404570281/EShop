package com.courtney.eshop

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.row_item.view.*

class ItemHolder(view: View) : RecyclerView.ViewHolder(view){
    var titleText = view.txt_title
    var priceText = view.txt_price
    var image = view.img_item
    fun onBind(item: Item) {
        titleText.text = item.title
        priceText.text = item.price.toString()
        Glide.with(itemView.context)
            .load(item.imageUrl)
            .apply(RequestOptions().override(300))
            .into(image)
    }
}