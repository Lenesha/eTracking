package com.airtec.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.airtec.R
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(this, Observer {
//            textView.text = it
//        })
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tripDetailsBtn.setOnClickListener({v-> callTripDetailsFragment(R.id.nav_trip_details_action)})
        deliveryNoteBtn.setOnClickListener({callTripDetailsFragment(R.id.nav_delivery_details_action)})
        statusBtn.setOnClickListener({callTripDetailsFragment(R.id.nav_trip_status_action)})

    }

    private fun callTripDetailsFragment(navTripDetailsAction: Int) {

        view!!.findNavController().navigate(navTripDetailsAction)

    }
}