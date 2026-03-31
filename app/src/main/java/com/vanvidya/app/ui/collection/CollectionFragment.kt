package com.vanvidya.app.ui.collection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.vanvidya.app.R

class CollectionFragment : Fragment() {

    private lateinit var plantCollectionCard: CardView
    private lateinit var mushroomCollectionCard: CardView
    private lateinit var flowerCollectionCard: CardView

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
        mushroomCollectionCard = view.findViewById(R.id.mushroom_collection_card)
        flowerCollectionCard = view.findViewById(R.id.flower_collection_card)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        plantCollectionCard.setOnClickListener {
            Toast.makeText(
                context,
                "Plant Collection - Coming Soon!\nSave your favorite plants here",
                Toast.LENGTH_SHORT
            ).show()
        }

        mushroomCollectionCard.setOnClickListener {
            Toast.makeText(
                context,
                "Mushroom Collection - Coming Soon!\nSave identified mushrooms here",
                Toast.LENGTH_SHORT
            ).show()
        }

        flowerCollectionCard.setOnClickListener {
            Toast.makeText(
                context,
                "Flower Collection - Coming Soon!\nSave beautiful flowers here",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}