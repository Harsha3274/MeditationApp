// MainActivity.kt
package com.wodo.meditationapp

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.wodo.meditationapp.data.LoginBody
import com.wodo.meditationapp.data.ValidateEmailBody
import com.wodo.meditationapp.databinding.ActivityMainBinding
import com.wodo.meditationapp.databinding.ActivitySignUpBinding
import com.wodo.meditationapp.repository.AuthRepository
import com.wodo.meditationapp.utils.APIConsumer
import com.wodo.meditationapp.utils.APIService
import com.wodo.meditationapp.utils.VibrateView
import com.wodo.meditationapp.view.SignUp
import com.wodo.meditationapp.view_model.LoginActivityViewModel
import com.wodo.meditationapp.view_model.LoginActivityViewModelFactory
import java.lang.StringBuilder
import java.security.Key

class MainActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener, View.OnKeyListener {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mViewModel: LoginActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)

        mBinding.SignInBtn.setOnClickListener(this)
        mBinding.SignUpText.setOnClickListener(this)
        mBinding.EmailEt.onFocusChangeListener = this
        mBinding.PasswordEt.onFocusChangeListener = this
        mBinding.PasswordEt.setOnKeyListener(this)

        mViewModel = ViewModelProvider(this, LoginActivityViewModelFactory(AuthRepository(APIService.getService()), application)).get(LoginActivityViewModel::class.java)

        setupObservers()
    }

    private fun setupObservers() {
        mViewModel.getIsLoading().observe(this) {
            mBinding.progressBar.isVisible = it
        }

        mViewModel.getErrorMessage().observe(this) {
            val formErrorKeys = arrayOf("fullname", "email", "password")
            val message = StringBuilder()
            it.map { entry ->
                if (formErrorKeys.contains(entry.key)) {
                    when (entry.key) {
                        "email" -> {
                            mBinding.EmailTil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }
                        }
                        "password" -> {
                            mBinding.PasswordTil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }
                        }
                    }
                } else {
                    message.append(entry.value).append("\n")
                }
                if (message.isNotEmpty()) {
                    AlertDialog.Builder(this)
                        .setIcon(R.drawable.baseline_info_24)
                        .setTitle("INFORMATION")
                        .setMessage(message)
                        .setPositiveButton("Ok") { dialog, _ -> dialog!!.dismiss() }
                        .show()
                }
            }

        }
        mViewModel.getUser().observe(this) {
            if (it != null) {
                startActivity(Intent(this, Home::class.java))
            }
        }
    }

    private fun validateEmail(shouldUpdateView: Boolean = true, shouldVibrateView: Boolean = true): Boolean {
        var errorMessage: String? = null
        val value = mBinding.EmailEt.text.toString()
        if (value.isEmpty()) {
            errorMessage = "Email is required"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            errorMessage = "Email address is invalid"
        }

        if (errorMessage != null && shouldUpdateView) {
            mBinding.EmailTil.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrateView) VibrateView.vibrate(this@MainActivity, this)
            }
        }

        return errorMessage == null
    }

    private fun validatePassword(shouldUpdateView: Boolean = true, shouldVibrateView: Boolean = true): Boolean {
        var errorMessage: String? = null
        val value = mBinding.PasswordEt.text.toString()
        if (value.isEmpty()) {
            errorMessage = "Password is required"
        } else if (value.length < 6) {
            errorMessage = "Password must be 6 characters long"
        }

        if (errorMessage != null && shouldUpdateView) {
            mBinding.PasswordTil.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrateView) VibrateView.vibrate(this@MainActivity, this)
            }
        }

        return errorMessage == null
    }

    private fun validate(): Boolean {
        var isValid = true
        if (!validateEmail(shouldVibrateView = false)) isValid = false
        if (!validatePassword(shouldVibrateView = false)) isValid = false
        if (!isValid) VibrateView.vibrate(this, mBinding.cardView1)
        return isValid
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when (view.id) {
                R.id.EmailEt -> {
                    if (hasFocus) {
                        if (mBinding.EmailTil.isErrorEnabled) {
                            mBinding.EmailTil.isErrorEnabled = false
                        }
                    } else {
                        validateEmail()
                    }
                }
                R.id.PasswordEt -> {
                    if (hasFocus) {
                        if (mBinding.PasswordTil.isErrorEnabled) {
                            mBinding.PasswordTil.isErrorEnabled = false
                        }
                    } else {
                        validatePassword()
                    }
                }
            }
        }
    }

    private fun submitForm() {
        if (validate()) {
            //verify user credentials
            mViewModel.loginUser(LoginBody(mBinding.EmailEt.text!!.toString(), mBinding.PasswordEt.text!!.toString()))
        }
    }

    override fun onKey(view: View?, keyCode: Int, keyEvent: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent!!.action == KeyEvent.ACTION_UP) {
            submitForm()
        }
        return false
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.SignInBtn -> {
                    submitForm()
                }
                R.id.SignUpText -> {
                    startActivity(Intent(this, SignUp::class.java))
                }
            }
        }
    }
}
