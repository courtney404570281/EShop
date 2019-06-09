package com.courtney.eshop

data class Item(var title: String, var price: Int) {
    constructor() : this("", 0)
}