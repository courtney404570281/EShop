package com.courtney.eshop

data class Item(var title: String, var price: Int, var imageUrl: String) {
    constructor() : this("", 0, "")
}