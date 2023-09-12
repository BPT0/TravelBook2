package com.graduation.travelbook2

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.graduation.travelbook2.base.BaseActivity
import com.graduation.travelbook2.databinding.ActivityLoginBinding

class LoginActivity :
    BaseActivity<ActivityLoginBinding>() {

    override val TAG : String = LoginActivity::class.java.simpleName
    override val layoutRes: Int = R.layout.activity_login

    val auth = FirebaseAuth.getInstance() // 유저 만들기

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firebaseUser: FirebaseUser? = auth.currentUser
        val mDatabasRef = FirebaseDatabase.getInstance().reference.child("TravelBook2")

        binding.apply {

            etxEmail.apply {

            }

            etxPwd.apply {
                // 키보드가 입력될 때
                // if (etxPwd.text.toString().length < 6) {
                //                // 워닝 메시지 표시
                //                Toast.makeText(this@RegisterActivity, "비밀번호는 6자리 이상이어야 합니다..", Toast.LENGTH_SHORT).show()
                //            }
            }

            if (TextUtils.isEmpty(etxEmail.text.toString()) || TextUtils.isEmpty(etxPwd.text.toString())) {
                // binding.warning.visibility = View.VISIBLE // 워닝 메시지 표시
            }

            btnRegister.apply {
                setOnClickListener {
                    val sIntent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(sIntent)

                }
            }

            btnLogin.apply{
                setOnClickListener {
                    // 회원가입 처리 시작
                    val strEmail = etxEmail.text.toString()
                    val strPwd = etxPwd.text.toString()

                    // Firebase Auth 진행

                    auth.signInWithEmailAndPassword(strEmail, strPwd)
                        .addOnCompleteListener {task ->

                            if (task.isSuccessful){ // 로그인 성공시
                                // todo: db 규칙 설정
                                if (firebaseUser != null){
                                    Toast.makeText(this@LoginActivity, "로그인에 성공하셨습니다.", Toast.LENGTH_SHORT).show()

                                    val lIntent = Intent(this@LoginActivity, MainActivity::class.java)
                                    lIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(lIntent)
                                }
                            } else{
                                Log.e(TAG, task.exception.toString())
                                Toast.makeText(this@LoginActivity, "로그인에 실패하셨습니다.", Toast.LENGTH_SHORT).show()
                            }

                            /*// task 의 결과에 에러가 있으면 중복 이메일에 대한 에러 처리
                            try {
                                task.result
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(this@RegisterActivity, "이미 있는 이메일 형식입니다.", Toast.LENGTH_SHORT).show()
                            }*/
                        }

                }
            }
        }
    }
}