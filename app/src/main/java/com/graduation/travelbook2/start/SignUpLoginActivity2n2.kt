package com.graduation.travelbook2.start

import  android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.graduation.travelbook2.MainActivity
import com.graduation.travelbook2.MyApplication
import com.graduation.travelbook2.R
import com.graduation.travelbook2.base.BaseActivity
import com.graduation.travelbook2.databinding.ActivityRegisterLoginBinding
import com.graduation.travelbook2.loading.LoadingDialog

class SignUpLoginActivity2n2 : BaseActivity<ActivityRegisterLoginBinding>() {
    override val TAG : String = SignUpLoginActivity2n2::class.java.simpleName
    override val layoutRes: Int = R.layout.activity_register_login

    private val uAuth = FirebaseAuth.getInstance()
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLoginBtn()
    }

    private fun setLoginBtn() {
        binding.apply {
            // Firebase Auth 진행
            btnLogin.setOnClickListener {
                if (etxPwd.text.toString().length < 6) {
                        // 워닝 메시지 표시
                        Toast.makeText(this@SignUpLoginActivity2n2,
                            "비밀번호는 6자리 이상이어야 합니다..", Toast.LENGTH_SHORT).show()
                }else{
                    login()
                }
            }
        }
    }

    private fun login() {
        loadingDialog = LoadingDialog(this)
        loadingDialog.show()
        // 로그인 처리 시작
        val strEmail = intent.getStringExtra("strEmail")!!
        val strPwd = binding.etxPwd.text.toString()

        // Firebase Auth 진행
        Log.i(TAG, "$strEmail $strPwd")
        uAuth.signInWithEmailAndPassword(strEmail, strPwd)
            .addOnCompleteListener(this@SignUpLoginActivity2n2) { task ->
                if (task.isSuccessful){ // 로그인 성공시
                    Toast.makeText(this@SignUpLoginActivity2n2, "로그인에 성공하셨습니다.", Toast.LENGTH_SHORT).show()

                    // shared pref 로 앱 최초 실행 여부 기억
                    MyApplication.prefs.setString("isFirst", "true")

                    // shared pref 로 앱 로그인 여부 기억
                    MyApplication.prefs.setString("isLogined", "true")
                    MyApplication.prefs.setString("strEmail", strEmail)
                    MyApplication.prefs.setString("strPwd", strPwd)

                    loadingDialog.dismiss()
                    val lIntent = Intent(this@SignUpLoginActivity2n2, MainActivity::class.java)
                    lIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(lIntent)
                } else{
                    Log.i(TAG, task.result.toString())
                    Log.e(TAG, task.exception.toString())
                    Toast.makeText(this@SignUpLoginActivity2n2, "로그인에 실패하셨습니다.", Toast.LENGTH_SHORT).show()
                }
        }
    }
}