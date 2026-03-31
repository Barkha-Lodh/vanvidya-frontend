package com.vanvidya.app.ui.diagnose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.vanvidya.app.R

class DiagnoseFragment : Fragment() {

    private lateinit var plantDiagnosisCard: CardView
    private lateinit var scanNowButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_diagnose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        plantDiagnosisCard = view.findViewById(R.id.plant_diagnosis_card)
        scanNowButton = view.findViewById(R.id.scan_now_button)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Clicking card or button both navigate to scan
        val navigateToScan = {
            try {
                findNavController().navigate(R.id.scanFragment)
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Opening camera for disease detection...",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        plantDiagnosisCard.setOnClickListener {
            navigateToScan()
        }

        scanNowButton.setOnClickListener {
            navigateToScan()
        }
    }
}