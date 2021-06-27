package com.casan.smokearea

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.casan.smokearea.databinding.ActivityUserSmokeBinding
import kotlin.concurrent.thread

class UserSmoke : AppCompatActivity() {
    private lateinit var bind: ActivityUserSmokeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityUserSmokeBinding.inflate(layoutInflater)
        setContentView(bind.root)

        supportActionBar?.hide()

        val db = AppDatabase.getInstance(this)
        val adapter = UserSmokeAdapter()

        // adapter 먼저 적용
        bind.UserSmokeRecyclerView.adapter = adapter
        bind.UserSmokeRecyclerView.layoutManager = LinearLayoutManager(this)

        // db load 되는대로 list 갱신
        thread {
            db!!.userDao().getTimeAll().run {
                adapter.submitList(this)
            }
        }
    }
}