package com.vanvidya.app.ui.collection

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.vanvidya.app.R
import com.vanvidya.app.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.jvm.java

class CollectionFragment : Fragment() {

    private lateinit var plantCollectionCard: CardView
    private lateinit var mushroomCollectionCard: CardView
    private lateinit var flowerCollectionCard: CardView
    private val db by lazy { AppDatabase.getDatabase(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_collection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        plantCollectionCard = view.findViewById(R.id.plant_collection_card)

        plantCollectionCard.setOnClickListener { openCollection("plant") }

        updateCounts()
    }

    private fun updateCounts() {
        lifecycleScope.launch {
            val plantCount = withContext(Dispatchers.IO) {
                db.plantDao().getCountByCategory("plant")
            }

            view?.findViewById<TextView>(R.id.plant_count)?.text =
                "$plantCount collected"
        }
    }

    private fun openCollection(type: String) {
        startActivity(Intent(requireContext(), CollectionListActivity::class.java).apply {
            putExtra("type", type)
        })
    }

    override fun onResume() {
        super.onResume()
        updateCounts()
    }
}