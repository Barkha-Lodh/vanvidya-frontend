package com.vanvidya.app.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.vanvidya.app.MainActivity
import com.vanvidya.app.R

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var nextButton: MaterialButton
    private lateinit var skipButton: MaterialButton

    private val onboardingPages = listOf(
        OnboardingPage(
            R.drawable.onboarding_care,
            "Plant Care Guide",
            "Get care guides and reminders specific to your plants"
        ),
        OnboardingPage(
            R.drawable.onboarding_growth,
            "Track Your Plant's growth",
            "Create a timeline for your plants and check their growth overtime."
        ),
        //OnboardingPage(
          //  R.drawable.onboarding_diagnose,
            //"Diagnose any diseases",
            //"Step-by-step diagnosis: Select the affected part, identify the issues, determine the disease, and explore treatment options."
        //)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if first launch
        val prefs = getSharedPreferences("vanvidya_prefs", MODE_PRIVATE)
        if (prefs.getBoolean("onboarding_completed", false)) {
            navigateToMain()
            return
        }

        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.view_pager)
        nextButton = findViewById(R.id.next_button)
        skipButton = findViewById(R.id.skip_button)

        viewPager.adapter = OnboardingAdapter(onboardingPages)

        nextButton.setOnClickListener {
            if (viewPager.currentItem < onboardingPages.size - 1) {
                viewPager.currentItem += 1
            } else {
                completeOnboarding()
            }
        }

        skipButton.setOnClickListener {
            completeOnboarding()
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == onboardingPages.size - 1) {
                    nextButton.text = "Get Started"
                } else {
                    nextButton.text = "Next"
                }
            }
        })
    }

    private fun completeOnboarding() {
        getSharedPreferences("vanvidya_prefs", MODE_PRIVATE)
            .edit()
            .putBoolean("onboarding_completed", true)
            .apply()

        navigateToMain()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

data class OnboardingPage(
    val imageRes: Int,
    val title: String,
    val description: String
)