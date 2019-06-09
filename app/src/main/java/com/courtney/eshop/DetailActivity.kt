package com.courtney.eshop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_detail.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class DetailActivity : AppCompatActivity(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val item = intent.getParcelableExtra<Item>("ITEM")
        info { "onCreate: ${item.id} ${item.title}" }
        web.settings.javaScriptEnabled = true
        web.loadUrl("https://shopee.tw/%EF%BD%9C%F0%9F%92%8B-N%C2%B0-%F0%9F%85%BA%F0%9F%85%BE%F0%9F%86%81%F0%9F%85%B4%F0%9F%85%B0-%E5%85%8D%E7%A8%85%E5%BA%97%E4%BB%A3%E8%B3%BC%EF%BD%9C%E7%8F%BE%E8%B2%A8-POWER-KISS-MAC-%E5%AD%90%E5%BD%88%E5%94%87%E8%86%8F-3g-i.17506481.1735756207")
    }
}
