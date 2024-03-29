package com.airtec.ui.aboutus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.airtec.R

class AboutusFragment : Fragment() {

    private lateinit var sendViewModel: AboutusViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        sendViewModel =
                ViewModelProviders.of(this).get(AboutusViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_aboutus, container, false)
        val textView: TextView = root.findViewById(R.id.text_send)
        sendViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}