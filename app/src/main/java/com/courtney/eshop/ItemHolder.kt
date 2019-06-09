package com.courtney.eshop

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_item.view.*

class ItemHolder(view: View) : RecyclerView.ViewHolder(view){
    var titleText = view.txt_title
    var priceText = view.txt_price
    fun onBind(item: Item) {
        titleText.text = item.title
        priceText.text = item.price.toString()
    }
}