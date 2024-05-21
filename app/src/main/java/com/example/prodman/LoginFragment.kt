package com.example.prodman

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.prodman.model.ProdViewModelFactory
import com.example.product.databinding.FragmentLoginBinding


import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.example.prodman.model.ProdViewModel


class LoginFragment : Fragment(){
    private val viewModel: ProdViewModel by activityViewModels {
        ProdViewModelFactory(
            (activity?.application as ProductApplication).database.productDao(),
            (activity?.application as ProductApplication).database.prodVersionDao(),
            (activity?.application as ProductApplication).database.prodVersionBatchDao(),
            (activity?.application as ProductApplication).database.prodBatchStepDao(),
            (activity?.application as ProductApplication).database.hazardDao(),
            (activity?.application as ProductApplication).database.measureDao(),
            (activity?.application as ProductApplication).database.userDao(),
            activity?.application as ProductApplication

        )
    }


    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!






    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    //private val userDao:UserDao
    //private lateinit var buttonSignUp: Button
    //private lateinit var dbHelper: DatabaseHelper


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fragmentName="FragmentLogin"
        //val adapter = LocalProdAdapter(ProdClick {})

        var loggedIn =  viewModel.checkIfLoggedIn()

        if (loggedIn) {
            // Successful login, proceed to next activity
            //startActivity(Intent(this, MainActivity::class.java))
            //finish()
            //Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
            val action = LoginFragmentDirections.actionLoginFragmentToProductListFragment()
            //    = ProductListFragmentDirections.actionProductListFragmentToProductDetailFragment(it.id)
            this.findNavController().navigate(action)
        }
        editTextUsername = binding.usernameEditText
        editTextPassword = binding.passwordEditText
        buttonLogin = binding.loginButton
//        buttonSignUp = binding.

        // Initialize Database Helper
        // dbHelper = DatabaseHelper(this)

        // Set click listener for the login button
        buttonLogin.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()

            // Attempt to login using local SQLite database
             loggedIn =  viewModel.attemptLogin(username, password, requireContext())

            if (loggedIn) {
                // Successful login, proceed to next activity
                //startActivity(Intent(this, MainActivity::class.java))
                //finish()
                //Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                 val action = LoginFragmentDirections.actionLoginFragmentToProductListFragment()
                 //    = ProductListFragmentDirections.actionProductListFragmentToProductDetailFragment(it.id)
                this.findNavController().navigate(action)

                //val action = loginfr

            }
            else {
              Toast.makeText(requireContext(), "Incorrect user name or password", Toast.LENGTH_LONG)
            }

        }

        val passwordEditText= binding.passwordEditText
        val showPasswordCheckBox= binding.checkBox

        // Set listener for checkbox state changes
        showPasswordCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            // If the checkbox is checked, show the password
            // Otherwise, hide the password
            if (isChecked) {
                passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }



    }



}
