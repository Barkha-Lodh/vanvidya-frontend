package com.vanvidya.app.ui.chatbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.vanvidya.app.R

class ChatBoxFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chatbox, container, false)
        view.findViewById<TextView>(R.id.tvPlaceholder).text = "Chat with AI - Coming Soon!"
        return view
    }
}