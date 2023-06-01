package com.TechFerbots.sketch.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.sketch.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashScreen : Fragment() {

    lateinit var auth :FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = auth.currentUser

        val runnable = Runnable {
            if (currentUser == null){
                val action = SplashScreenDirections.actionSplashScreenToLoginScreen2()
                findNavController().navigate(action)
            }
            else{
                val action = SplashScreenDirections.actionSplashScreenToSketchListing()
                findNavController().navigate(action)
            }
        }
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(runnable, 2500)
    }

}