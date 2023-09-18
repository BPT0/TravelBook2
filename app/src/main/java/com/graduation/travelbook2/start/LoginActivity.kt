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
import org.jetbrains.anko.email

class LoginActivity :
    BaseActivity<ActivityLoginBinding>() {

    override val TAG : String = LoginActivity::class.java.simpleName
    override val layoutRes: Int = R.layout.activity_login

    private var auth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        val emailId = intent.getStringExtra("emailId")
        val pwd = intent.getStringExtra("password")

        binding.apply {
            if (!emailId.isNullOrBlank() && !pwd.isNullOrEmpty()){
                etxEmail.setText(emailId)
                etxPwd.setText(pwd)
                invalidateAll()
                btnLogin.performClick()
            }

            etxEmail.apply {

            }

            etxPwd.apply {
                // 키보드가 입력될 때
                // if (etxPwd.text.toString().length < 6) {
                //                // 워닝 메시지 표시
                //                Toast.makeText(this@RegisterActivity, "비밀번호는 6자리 이상이어야 합니다..", Toast.LENGTH_SHORT).show()
                //            }
            }

            if (TextUtils.isEmpty(etxPwd.text.toString()) || TextUtils.isEmpty(etxEmail.text.toString())) {
                // binding.warning.visibility = View.VISIBLE // 워닝 메시지 표시
            }

            btnRegister.apply {
                setOnClickListener {
                    val sIntent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(sIntent)

                }
            }

            btnLogin.apply{
                setOnClickListener {
                    // 로그인 처리 시작
                    val strEmail = etxEmail.text.toString()
                    val strPwd = etxPwd.text.toString()

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
        }
    }
}