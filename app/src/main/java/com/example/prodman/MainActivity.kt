/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.prodman

import android.Manifest
//import android.R
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.example.prodman.model.ProdViewModel
import com.example.product.R
import com.example.product.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONArray
import org.json.JSONObject
import org.vosk.LibVosk
import org.vosk.LogLevel
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.SpeechStreamService
import org.vosk.android.StorageService
import java.io.IOException


class MainActivity : AppCompatActivity(R.layout.activity_main),  RecognitionListener {

    private lateinit var navController: NavController

    private var model: Model? = null
    private var speechService: SpeechService? = null
    private var speechStreamService: SpeechStreamService? = null
    private var resultView: TextView? = null
    private var heard:TextInputEditText? = null
    private var uistate:Int=0
    private lateinit  var fab: FloatingActionButton


    private lateinit var feb: FloatingActionButton
    val viewModel: ProdViewModel by viewModels()


    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!


    private val STATE_START = 0
    private val STATE_READY = 1
    private val STATE_DONE = 2
    private val STATE_FILE = 3
    private val STATE_MIC = 4

    /* Used to handle permission request */
    private val PERMISSIONS_REQUEST_RECORD_AUDIO = 1




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        // Retrieve NavController from the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        // Set up the action bar for use with the NavController
        setupActionBarWithNavController(this, navController)





        // binding.floatingActionButton.setOnClickListener {
       //     recognizeMicrophone()  }

        // Setup layout
        //resultView = binding.resultText
       // resultView = binding.resultText


        heard = findViewById<TextInputEditText>(R.id.heard)
        heard!!.isVisible=false
        //viewModel.listening = false

        feb = findViewById<FloatingActionButton>(R.id.floatingExitButton)
        feb.setOnClickListener {
            //finish() // This will finish the current activity and close the app
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Exit S7ProdTrac")
            builder.setMessage("Leave the application?")
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

            builder.setPositiveButton("Cancel") { dialog, which ->
                //Toast.makeText(applicationContext,
                 //   "Cancel ", Toast.LENGTH_SHORT).show()
            }

            builder.setNegativeButton("Just Exit") { dialog, which ->
                finish()
            }

            builder.setNeutralButton("Logout and Exit") { dialog, which ->
                viewModel.logout()
                finish()
                 }
            builder.show()
        }

        fab = findViewById<FloatingActionButton>(R.id.floatingActionButton)



        fab.setOnClickListener {
               if (!(viewModel.listening)) {
                   fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0000FF")))

                   heard?.isVisible = true
                   recognizeMicrophone()
                   viewModel.listening=true
               }
            else
               {
                   fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#f0f0f0")))

                   heard?.isVisible = false
                   viewModel.listening=false
                   //recognizeMicrophone()

               }

              }

        LibVosk.setLogLevel(LogLevel.INFO)
                    // Check if user has given permission to record audio, init the model after permission is granted

                            val permissionCheck =
                                ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.RECORD_AUDIO)

                        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {


                              ActivityCompat.requestPermissions(
                                  this,
                                  arrayOf(Manifest.permission.RECORD_AUDIO),
                                  MainActivity.PERMISSIONS_REQUEST_RECORD_AUDIO
                              )

                          } else {
                            initModel()
                        }

    }

    private fun recognizeMicrophone() {
        if (speechService != null) {
            setUiState(STATE_DONE)
            speechService!!.stop()
            speechService = null
        } else {
            setUiState(STATE_MIC)
            try {
                val rec = Recognizer(model, 16000.0f)
                speechService = SpeechService(rec, 16000.0f)
                speechService!!.startListening(this)
            } catch (e: IOException) {
                setErrorState(e.message)
            }
        }
    }
    private fun initModel() {

        StorageService.unpack(this, "model-en-us", "model",
            { model: Model? ->
                this.model = model
                setUiState(STATE_READY)
            }
        ) { exception: IOException ->
            setErrorState(
                "Failed to unpack the model" + exception.message
            )
        }

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MainActivity.PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Recognizer initialization is a time-consuming and it involves IO,
                // so we execute it in async task
                initModel()
            } else {
                // finish()
            }
        }
    }
    public override fun onDestroy() {
        super.onDestroy()
        if (speechService != null) {
            speechService!!.stop()
            speechService!!.shutdown()
        }
        if (speechStreamService != null) {
            speechStreamService!!.stop()
        }
    }
    override fun onResult(hypothesis: String) {

        val jsonObj = JSONObject(hypothesis)
        val map = jsonObj.toMap()
        var data: String = map["text"].toString()
        when(viewModel.fragmentName) {
            "StepDetailFragment" -> {

                   if ((data=="checked") or (data=="sec"))
                       data ="check"
                   if (data =="school")
                       data ="score"
                   if ((data=="without") or (data=="he thought") or (data=="he wrote") or (data=="he out"))
                       data ="result"
                   if (("the to" in data) or (data=="details")or (data=="deal") or (data=="the too") or (data=="you too"))
                       data = "detail"
                heard!!.setText(data)
                //var stepDetailFragment:StepDetailFragment? = supportFragmentManager.findFragmentById(R.id.stepDetailFragment) as StepDetailFragment?
                       when(data){
                     "check"->{ viewModel.stepInputMode="check"
                                  data=""}
                    "score"->{ viewModel.stepInputMode="score"
                               data=""}
                    "result"->{ viewModel.stepInputMode="result"
                               data=""}
                    "detail"->{ viewModel.stepInputMode="detail"
                               data=""}
                    "stop"->{ viewModel.stepInputMode="none"
                                data=""}
                    "back"->{
                        viewModel.stepInputMode="none"
                        val action =
                            StepDetailFragmentDirections.actionStepDetailFragmentToBatchStepListFragment(
                                viewModel.batch
                            )
                        navController.navigate(action)
                    }
                    else->{
                        if (data.length>0) {
                            when (viewModel.stepInputMode) {
                                "check" -> {
                                    val checkedInputEditText: TextInputEditText =
                                        findViewById(R.id.checked_by)

                                    checkedInputEditText.setText(data)
                                    viewModel.stepInputMode = "none"

                                }

                                "score" -> {
                                    val scoreInputEditText: TextInputEditText =
                                        findViewById(R.id.new_hazard)
                                    scoreInputEditText.setText(data)
                                    viewModel.stepInputMode = "none"
                                }

                                "result" -> {
                                    val resultInputEditText: TextInputEditText =
                                        findViewById(R.id.reading)
                                    resultInputEditText.setText(data)
                                    viewModel.stepInputMode = "none"
                                }

                                "detail" -> {
                                    val detailInputEditText: TextInputEditText =
                                        findViewById(R.id.reading)
                                    detailInputEditText.append(data)
                                }


                            }
                        }
                    }

                 }

            }

            else -> {


                if ("command" in data)
                    data = "command"
                if ("comment" in data)
                    data = "command"
                if ("come" in data)
                    data = "command"
                if ("man" in data)
                    data = "command"

                if (data == "step")
                    data = "stop"

                if ((data == "with") or (data == "us"))
                    data = "yes"

                when (data) {
                    "command" -> {
                        setUiState(STATE_ENTRY);
                        fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")))
                        heard!!.setText("")
                    }

                    "stop" -> {
                        setUiState(STATE_READY);
                        fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFBB86FC")))
                        heard!!.setText("")


                    }

                    "yes" -> {


                        val selNum: Int = getNumFromText(heard!!)
                        if ((selNum > 0) && (selNum < 10)) {
                            // Now, you have the name of the top fragment in the back stack.
                            // You can use this name to identify the fragment if needed.

                            // You can also retrieve the actual Fragment instance:
                            //  val topFragment = fragmentManager.findFragmentByTag(fragmentName)
                            when (viewModel.fragmentName) {
                                "ProductListFragment" -> {
                                    //val productFragment = yourFragment as? ProductListFragment
                                    //productFragment?.view?

                                    val action =
                                        ProductListFragmentDirections.actionProductListFragmentToProductDetailFragment(
                                            selNum
                                        )
                                    navController.navigate(action)

                                }

                                "ProductDetailFragment" -> {
                                    // val productDetailFragment:ProductDetailFragment? =yourFragment as? ProductDetailFragment?

                                    val action =
                                        ProductDetailFragmentDirections.actionProductDetailFragmentToBatchListFragment(
                                            selNum
                                        )
                                    navController.navigate(action)


                                }

                                "BatchListFragment" -> {

                                    val action =
                                        BatchListFragmentDirections.actionBatchListFragmentToBatchStepListFragment(
                                            selNum
                                        )
                                    navController.navigate(action)
                                }

                                "BatchStepListFragment" -> {

                                    val action =
                                        BatchStepListFragmentDirections.actionBatchStepListFragmentToStepDetailFragment(
                                            selNum
                                        )
                                    navController.navigate(action)
                                }


                            }
                            heard!!.setText("")

                            //   heard!!.append(" " + fragmentName)// This fragment is currently displayed.


                        }

                    }

                    "back" -> {

                              val yourFragment =
                            supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.childFragmentManager?.primaryNavigationFragment

                        val selNum: Int = getNumFromText(heard!!)
                        var fragmentName = ""

                        if (yourFragment is ProductListFragment)
                            fragmentName = "ProductListFragment"
                        if (yourFragment is ProductDetailFragment)
                            fragmentName = "ProductDetailFragment"

                        if (yourFragment is BatchListFragment)
                            fragmentName = "BatchListFragment"
                        if (yourFragment is BatchStepListFragment)
                            fragmentName = "BatchStepListFragment"
                        if (yourFragment is StepDetailFragment)
                            fragmentName = "StepDetailFragment"
                        // Now, you have the name of the top fragment in the back stack.
                        // You can use this name to identify the fragment if needed.

                        // You can also retrieve the actual Fragment instance:
                        //  val topFragment = fragmentManager.findFragmentByTag(fragmentName)
                        when (fragmentName) {
                            "ProductDetailFragment" -> {

                                val action =
                                    ProductDetailFragmentDirections.actionProductDetailFragmentToProductListFragment()
                                navController.navigate(action)
                            }

                            "BatchListFragment" -> {

                                val action =
                                    BatchListFragmentDirections.actionBatchListFragmentToProductDetailFragment(
                                        viewModel.prodId
                                    )
                                navController.navigate(action)
                            }

                            "BatchStepListFragment" -> {

                                val action =
                                    BatchStepListFragmentDirections.actionBatchStepListFragmentToBatchListFragment(
                                        viewModel.version
                                    )
                                navController.navigate(action)
                            }

                            "StepDetailFragment" -> {

                                val action =
                                    StepDetailFragmentDirections.actionStepDetailFragmentToBatchStepListFragment(
                                        viewModel.batch
                                    )
                                navController.navigate(action)
                            }
                        }
                        heard!!.setText("")

                        //   heard!!.append(" " + fragmentName)// This fragment is currently displayed.


                    }


                    else -> {
                        when (uistate) {
                            MainActivity.STATE_ENTRY -> {
                                if (heard!!.text!!.length == 0)
                                    heard!!.setText(data)
                                else
                                    heard!!.append(" " + data)

                            }

                            else -> {
                                heard!!.setText(data)
                            }

                        }
                    }
                }

            }   //any other command - not in stepdetail

        }
    }
    override fun onFinalResult(hypothesis: String) {

        heard!!.setText(
            """
             $hypothesis
             
             """.trimIndent()
        )
        setUiState(STATE_DONE)
        if (speechStreamService != null) {
            speechStreamService = null
        }
    }
    override fun onPartialResult(hypothesis: String) {
    }
    override fun onError(e: Exception) {
        setErrorState(e.message)
    }
    override fun onTimeout() {
        setUiState(STATE_DONE)
    }
    private fun setUiState(state: Int) {
        uistate=state
        when (state) {
            STATE_START -> {
                 fab.rippleColor= Color.BLUE
                resultView?.setText(R.string.preparing)
                resultView!!.movementMethod = ScrollingMovementMethod()
                fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0000FF")))

            }
            STATE_READY -> {
                resultView?.setText(R.string.ready)
                fab.rippleColor= Color.RED

            }
            STATE_DONE -> {
                fab.rippleColor= Color.GREEN
            }
            STATE_FILE -> {
                fab.rippleColor= Color.BLACK

            }
            STATE_MIC -> {
                fab.rippleColor= Color.DKGRAY

            }
           STATE_ENTRY-> {
               fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")))
           }



            else -> throw IllegalStateException("Unexpected value: $state")
        }
    }
    private fun setErrorState(message: String?) {

    }
    companion object {
        private const val STATE_START = 0
        private const val STATE_READY = 1
        private const val STATE_DONE = 2
        private const val STATE_FILE = 3
        private const val STATE_MIC = 4
        private const val STATE_ENTRY = 5
        private const val STATE_GO= 6

        private const val PERMISSIONS_REQUEST_RECORD_AUDIO = 1
    }



    override fun onSupportNavigateUp(): Boolean {
        ///
        /*
        val fragmentManager = supportFragmentManager
        val currentFragment = fragmentManager.findFragmentById(R.id.nav_host_fragment) // Replace with your fragment container ID
        val fragmentTransaction = fragmentManager.beginTransaction()
        if (currentFragment != null) {
            // Check if the fragment is in the back stack
            if (fragmentManager.backStackEntryCount > 0) {
                // Pop the back stack to remove the top fragment
                fragmentManager.popBackStack()
            } else {
                // If not in the back stack, simply remove it
                fragmentTransaction.remove(currentFragment).commit()
            }
            return true
        }
       */
        ///
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


    fun JSONObject.toMap(): Map<String, *> = keys().asSequence().associateWith {
        when (val value = this[it])
        {
            is JSONArray ->
            {
                val map = (0 until value.length()).associate { Pair(it.toString(), value[it]) }
                JSONObject(map).toMap().values.toList()
            }
            is JSONObject -> value.toMap()
            JSONObject.NULL -> null
            else            -> value
        }
    }


    fun getNumFromText(heard:TextInputEditText):Int{
        val str:String = heard.text.toString()
        var num:Int=0

        when(str){
            "one"->{num=1}
            "two"->{num=2}
            "three"->{num=3}
            "four"->{num=4}
            "five"->{num=5}
            "six"->{num=6}
            "seven"->{num=7}
            "eight"->{num=8}
            "nine"->{num=9}
            "ten"->{num=10}
                else-> {
                    val toast = Toast.makeText(this, "invalid command", Toast.LENGTH_LONG) // in Activity
                    toast.show()

                }
        }
        return num
    }

}
