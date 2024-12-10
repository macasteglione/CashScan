package com.example.reconocimiento_billetes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reconocimiento_billetes.presentation.ModelListAdapter

class ConfigActivity : AppCompatActivity() {

    private lateinit var modelListRecyclerView: RecyclerView
    private lateinit var selectModelButton: Button
    private lateinit var modelListAdapter: ModelListAdapter
    private var selectedModelIndex: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        val modelNames = resources.getStringArray(R.array.model_names)

        selectedModelIndex = intent.getIntExtra("selectedModelIndex", -1)

        modelListRecyclerView = findViewById(R.id.model_list)
        selectModelButton = findViewById(R.id.select_model_button)

        modelListRecyclerView.layoutManager = LinearLayoutManager(this)

        modelListAdapter = ModelListAdapter(modelNames.toList()) { _, position ->
            selectedModelIndex = position
        }

        modelListRecyclerView.adapter = modelListAdapter

        selectedModelIndex?.let { index ->
            if (index >= 0) modelListAdapter.setSelectedPosition(index)
        }

        selectModelButton.setOnClickListener {
            selectedModelIndex?.let { index ->
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("selectedModelIndex", index)
                startActivity(intent)
                finish()
            }
        }
    }
}
