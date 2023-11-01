package com.graduation.travelbook2.start

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.graduation.travelbook2.MainActivity
import com.graduation.travelbook2.MyApplication
import com.graduation.travelbook2.R
import com.graduation.travelbook2.base.BaseActivity
import com.graduation.travelbook2.databinding.ActivityLoginBinding

class LoginActivity :
    BaseActivity<ActivityLoginBinding>() {

    override val TAG : String = LoginActivity::class.java.simpleName
    override val layoutRes: Int = R.layout.activity_login

    private var auth : FirebaseAuth? = null

    private var emailId = ""
    private var pwd = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        emailId = intent.getStringExtra("emailId")!!
        pwd = intent.getStringExtra("password")!!

        setLogin()
        setAutoLogin()
        setSignupBtn()
    }

    private fun setSignupBtn() {
        binding.btnRegister.setOnClickListener {
            val sIntent = Intent(this@LoginActivity, Register1Activity::class.java)
            startActivity(sIntent)
        }
    }


    private fun setAutoLogin() {
        binding.apply {
            if (!emailId.isNullOrBlank() && !pwd.isNullOrEmpty()){
                etxEmail.setText(emailId)
                etxPwd.setText(pwd)
                invalidateAll()
                btnLogin.performClick()
            }
        }
    }

    private fun setLogin() {
        showLoginError()
        setLoginBtn()
    }

    private fun showLoginError() {
        // todo: 정규표현식 사용해서 이메일 형식 필터링
        // 비밀번호의 입력값이 6자 이하일때, text가 입력되지 않았을때

        if (binding.etxPwd.text.toString().length < 6
            && TextUtils.isEmpty(binding.etxEmail.text.toString())) {
            // 워닝 메시지 표시
            Toast.makeText(
                this@LoginActivity,
                "비밀번호는 6자리 이상이어야 합니다..", Toast.LENGTH_SHORT
            ).show()
        }else{
            login()
        }
    }

    private fun setLoginBtn() {
        binding.btnLogin.setOnClickListener {
            // 로그인 처리 시작
            val strEmail = binding.etxEmail.text.toString()
            val strPwd = binding.etxPwd.text.toString()

            // Firebase Auth 진행
            auth?.signInWithEmailAndPassword(strEmail, strPwd)?.addOnCompleteListener {task ->
                if (task.isSuccessful){ // 로그인 성공시
                    Toast.makeText(this@LoginActivity, "로그인에 성공하셨습니다.", Toast.LENGTH_SHORT).show()

                    MyApplication.prefs.setString("isLogined", "true")
                    MyApplication.prefs.setString("strEmail", strEmail)
                    MyApplication.prefs.setString("strPwd", strPwd)

                    val lIntent = Intent(this@LoginActivity, MainActivity::class.java)
                    lIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(lIntent)
                    finish()
                } else{
                    Log.e(TAG, task.exception.toString())
                    Toast.makeText(this@LoginActivity, "로그인에 실패하셨습니다.", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun login() {

    }
}