package com.example.mysimplescanner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class DatabaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database)

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish() // กลับหน้าก่อนหน้า
        }
    }
}
