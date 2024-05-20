package com.wodo.meditationapp.view

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.wodo.meditationapp.Home
import com.wodo.meditationapp.MainActivity
import com.wodo.meditationapp.R
import com.wodo.meditationapp.data.RegisterBody
import com.wodo.meditationapp.data.ValidateEmailBody
import com.wodo.meditationapp.databinding.ActivitySignUpBinding
import com.wodo.meditationapp.repository.AuthRepository
import com.wodo.meditationapp.utils.APIService
import com.wodo.meditationapp.utils.VibrateView
import com.wodo.meditationapp.view_model.RegisterActivityViewModel
import com.wodo.meditationapp.view_model.RegisterActivityViewModelFactory
import java.lang.StringBuilder
import java.security.Key
import kotlin.math.log

class SignUp : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener, View.OnKeyListener, TextWatcher{
    private lateinit var mBinding: ActivitySignUpBinding
    private lateinit var mViewModel: RegisterActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding=ActivitySignUpBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)
        mBinding.FullNameEt.setOnFocusChangeListener(this)
        mBinding.EmailEt.onFocusChangeListener=this
        mBinding.PasswordEt.onFocusChangeListener=this
        mBinding.ConfirmPasswordEt.onFocusChangeListener=this
        mBinding.ConfirmPasswordEt.setOnKeyListener(this)
        mBinding.ConfirmPasswordEt.addTextChangedListener(this)
        mBinding.SignUpBtn.setOnKeyListener(this)
        mViewModel=ViewModelProvider(this, RegisterActivityViewModelFactory(AuthRepository(APIService.getService()), application)).get(RegisterActivityViewModel::class.java)
        setupObservers()
    }

    private fun setupObservers(){
        mViewModel.getIsLoading().observe(this){
    mBinding.progressBar.isVisible=it
        }

        mViewModel.getIsUniqueEmail().observe(this){
            if (validateEmail(shouldUpdateView = false)){
            if(it){
                mBinding.EmailTil.apply {
                    if(isErrorEnabled) isErrorEnabled=false
                    setStartIconDrawable(R.drawable.baseline_check_circle_24)
                    setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                }
            }else{
                mBinding.EmailTil.apply {
                    if(startIconDrawable!=null) startIconDrawable=null
                    isErrorEnabled=true
                    error="Email is already taken"
                }
            }
        }
        }


        mViewModel.getErrorMessage().observe(this){
            val formErrorKeys= arrayOf("fullname","email","password")
            val message= StringBuilder()
            it.map { entry ->
                if(formErrorKeys.contains(entry.key)){
                    when(entry.key){
                        "fullname"->{
                                mBinding.FullNameTil.apply {
                                    isErrorEnabled=true
                                    error=entry.value
                                }
                        }
                        "email"->{
                                mBinding.EmailTil.apply {
                                    isErrorEnabled=true
                                    error=entry.value
                                }
                        }
                        "password"->{
                                mBinding.PasswordTil.apply {
                                    isErrorEnabled=true
                                    error=entry.value
                                }
                        }
                    }
                }else{
                        message.append(entry.value).append("\n")
                }
                if(message.isNotEmpty()){
                    AlertDialog.Builder(this)
                        .setIcon(R.drawable.baseline_info_24)
                        .setTitle("INFORMATION")
                        .setMessage(message)
                        .setPositiveButton("Ok"){dialog, _ ->dialog!!.dismiss()}
                        .show()
                }
            }

        }
        mViewModel.getUser().observe(this){
            if(it!=null){
                startActivity(Intent(this,Home::class.java))
            }
        }
    }

    private fun validateFullName(shouldVibrateView: Boolean=true): Boolean {
        var errorMessage: String? = null
        val value: String = mBinding.FullNameEt.text.toString()
        if(value.isEmpty()){
            errorMessage="Provide Full Name"
        }

        if (errorMessage!=null){
            mBinding.FullNameTil.apply {
                isErrorEnabled=true
                error=errorMessage
                VibrateView.vibrate(this@SignUp,this)
            }
        }

        return errorMessage==null
    }

    private fun validateEmail(shouldUpdateView: Boolean=true, shouldVibrateView: Boolean=true):Boolean {
        var errorMessage: String? = null
        val value = mBinding.EmailEt.text.toString()
        if(value.isEmpty()){
            errorMessage="Email is required"
        }else if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()){
            errorMessage="Email address is invalid"
        }

        if (errorMessage!=null&&shouldUpdateView){
            mBinding.EmailTil.apply {
                isErrorEnabled=true
                error=errorMessage
                if (shouldVibrateView) VibrateView.vibrate(this@SignUp,this)
            }
        }

        return errorMessage==null
    }

    private fun validatePassword(shouldUpdateView: Boolean=true, shouldVibrateView: Boolean=true):Boolean {
        var errorMessage: String? = null
        val value = mBinding.PasswordEt.text.toString()
        if(value.isEmpty()){
            errorMessage="Password is required"
        }else if (value.length<6){
            errorMessage="Password must be 6 characters long"
        }

        if (errorMessage!=null && shouldUpdateView){
            mBinding.PasswordTil.apply {
                isErrorEnabled=true
                error=errorMessage
                if (shouldVibrateView) VibrateView.vibrate(this@SignUp,this)
            }
        }

        return errorMessage==null
    }

    private fun validateConfirmPassword(shouldUpdateView: Boolean=true, shouldVibrateView: Boolean=true):Boolean {
        var errorMessage: String? = null
        val value = mBinding.ConfirmPasswordEt.text.toString()
        if(value.isEmpty()){
            errorMessage="Confirm Password is required"
        }else if (value.length<6){
            errorMessage="Confirm Password must be 6 characters long"
        }

        if (errorMessage!=null && shouldUpdateView){
            mBinding.ConfirmPasswordTil.apply {
                isErrorEnabled=true
                error=errorMessage
                if (shouldVibrateView) VibrateView.vibrate(this@SignUp,this)
            }
        }

        return errorMessage==null
    }

    private fun validatePasswordAndConfirmPassword(shouldUpdateView: Boolean=true, shouldVibrateView: Boolean=true):Boolean {
        var errorMessage: String? = null
        val password = mBinding.PasswordEt.text.toString()
        val confirmPassword = mBinding.ConfirmPasswordEt.text.toString()
        if(password!=confirmPassword){
            errorMessage="Confirm Password didn't match with password"
        }
        if (errorMessage!=null && shouldUpdateView){
            mBinding.ConfirmPasswordTil.apply {
                isErrorEnabled=true
                error=errorMessage
                if (shouldVibrateView) VibrateView.vibrate(this@SignUp,this)
            }
        }
        return errorMessage==null
    }


    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.SignUpBtn -> onSubmit()
                R.id.SignInText -> startActivity(Intent(this, MainActivity::class.java)) // Handle click for SignInText
            }
        }
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
       if (view!=null){
           when(view.id){
               R.id.FullNameEt ->{
                   if (hasFocus){
                        if(mBinding.FullNameTil.isErrorEnabled){
                            mBinding.FullNameTil.isErrorEnabled=false
                        }
                   }else{
                       validateFullName()
                   }
               }
               R.id.EmailEt ->{
                   if (hasFocus){
                       if(mBinding.EmailTil.isErrorEnabled){
                           mBinding.EmailTil.isErrorEnabled=false
                       }
                   }else{
                       if (validateEmail()){
                           mViewModel.validateEmailAddress(ValidateEmailBody(mBinding.EmailEt.text!!.toString()))
                       }
                   }
               }
               R.id.PasswordEt ->{
                   if (hasFocus){
                       if(mBinding.PasswordTil.isErrorEnabled){
                           mBinding.PasswordTil.isErrorEnabled=false
                       }
                   }else{
                       if (validatePassword()&&mBinding.ConfirmPasswordEt.text!!.isNotEmpty()&&validateConfirmPassword()&&validatePasswordAndConfirmPassword()){
                           if (mBinding.ConfirmPasswordTil.isErrorEnabled){
                               mBinding.ConfirmPasswordTil.isErrorEnabled=false
                           }
                           mBinding.ConfirmPasswordTil.apply {
                               setStartIconDrawable(R.drawable.baseline_check_circle_24)
                               setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                           }
                       }
                   }
               }
               R.id.ConfirmPasswordEt ->{
                   if (hasFocus){
                       if(mBinding.ConfirmPasswordTil.isErrorEnabled){
                           mBinding.ConfirmPasswordTil.isErrorEnabled=false
                       }
                   }else{
                       if (validateConfirmPassword()&&validatePassword()&&validatePasswordAndConfirmPassword()){
                           if (mBinding.PasswordTil.isErrorEnabled){
                               mBinding.PasswordTil.isErrorEnabled=false
                           }
                           mBinding.ConfirmPasswordTil.apply {
                               setStartIconDrawable(R.drawable.baseline_check_circle_24)
                               setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                           }
                       }
                   }
               }
           }
       }
    }

    override fun onKey(view: View?, keyCode: Int, keyEvent: KeyEvent?): Boolean {
        if(KeyEvent.KEYCODE_ENTER == keyCode && keyEvent!!.action == KeyEvent.ACTION_UP){
            //do registration
        }
        return false
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (validatePassword(shouldUpdateView = false) && validateConfirmPassword(shouldUpdateView = false) && validatePasswordAndConfirmPassword(shouldUpdateView = false)){
            mBinding.ConfirmPasswordTil.apply {
                if(isErrorEnabled) isErrorEnabled=false
                setStartIconDrawable(R.drawable.baseline_check_circle_24)
                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
            }
        }else{
            if(mBinding.ConfirmPasswordTil.startIconDrawable!=null)
                mBinding.ConfirmPasswordTil.startIconDrawable=null
        }
    }

    override fun afterTextChanged(p0: Editable?) {
    }
    private fun onSubmit(){
        if(validate()){
            mViewModel.registerUser(RegisterBody(mBinding.FullNameEt.text!!.toString(),mBinding.EmailEt.text!!.toString(), mBinding.PasswordEt.text!!.toString()))
        }
    }
    private fun validate(): Boolean{
        var isValid=true
        if(!validateFullName(shouldVibrateView = false)) isValid=false
        if(!validateEmail(shouldVibrateView = false)) isValid=false
        if(!validatePassword(shouldVibrateView = false)) isValid=false
        if(!validateConfirmPassword(shouldVibrateView = false)) isValid=false
        if(isValid && !validatePasswordAndConfirmPassword(shouldVibrateView = false)) isValid=false

        if (!isValid) VibrateView.vibrate(this, mBinding.cardView)
        return isValid
    }
}