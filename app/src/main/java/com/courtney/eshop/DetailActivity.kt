package com.courtney.eshop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_detail.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class DetailActivity : AppCompatActivity(), AnkoLogger {

    lateinit var item: Item

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        item = intent.getParcelableExtra<Item>("ITEM")
        info { "onCreate: ${item.id} ${item.title}" }
        web.settings.javaScriptEnabled = true
        web.loadUrl(item.content)
    }

    override fun onStart() {
        super.onStart()
        item.viewCount++
        item.id?.let {
            FirebaseFirestore.getInstance().collection("items")
                .document(it).set(item)
        }
    }
}
