package com.syncday.lab

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.syncday.lab.databinding.ActivitySecondBinding

class SearchActivity: BaseActivity() {

    private lateinit var binding: ActivitySecondBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.list.apply {
            adapter = SecondAdapter().apply {
                submitList(homeBeanList)
            }
            layoutManager = LinearLayoutManager(this@SearchActivity)
        }

        binding.back.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }


}