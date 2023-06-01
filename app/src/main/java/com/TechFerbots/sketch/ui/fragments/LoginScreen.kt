package com.TechFerbots.sketch.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.sketch.R
import com.example.sketch.databinding.FragmentLoginScreenBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

const val GOOGLE_SIGNIN_REQ_CODE = 1

class LoginScreen : Fragment() {

    lateinit var auth :FirebaseAuth

    private var _binding: FragmentLoginScreenBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

         binding.googleLoginBtn.setOnClickListener {
             val googlesigninclient = GoogleSignIn.getClient(requireContext(),googleSignInOptions)
             googlesigninclient.signInIntent.also {
                 startActivityForResult(it, GOOGLE_SIGNIN_REQ_CODE)
             }
         }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //google signin
        if (requestCode == GOOGLE_SIGNIN_REQ_CODE){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful){
                try {
                    val account = task.getResult(ApiException::class.java)
                    googleAuthToFirebase(account)
                }
                catch (e : ApiException){
                    Log.v("MyActivity","google signin failed: ${e}")
                }
            }else{
                Log.v("MyActivity","abc${task.exception?.cause}")
            }
        }
        else{
        }

        /*when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with Firebase.
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            activity?.let {
                                auth.signInWithCredential(firebaseCredential)
                                    .addOnCompleteListener(it) { task ->
                                        if (task.isSuccessful) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.v("MyActivity", "signInWithCredential:success")
                                            val user = auth.currentUser
                                            Log.v("MyActivity", "${user!!.phoneNumber}")
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.v("MyActivity", "signInWithCredential:failure", task.exception)
                                        }
                                    }
                            }
                            Log.v("MyActivity", "Got ID token.")
                        }
                        else -> {
                            // Shouldn't happen.
                            Log.v("MyActivity", "No ID token!")
                        }
                    }
                } catch (e: ApiException) {
                    // ...
                }
            }
        }*/

    }

    private fun googleAuthToFirebase(account: GoogleSignInAccount?) {
        val credentials = GoogleAuthProvider.getCredential(account?.idToken!!,null)
        activity?.let {
            auth.signInWithCredential(credentials)
                .addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        val action = LoginScreenDirections.actionLoginScreenToSketchListing()
                        findNavController().navigate(action)
                    } else {
                        Log.w("MyActivity", "signInWithCredential:failure", task.exception)
                    }
                }
        }
    }


}