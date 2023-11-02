package com.graduation.travelbook2.start

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.graduation.travelbook2.MyApplication
import com.graduation.travelbook2.R
import com.graduation.travelbook2.externalDto.UserDto
import com.graduation.travelbook2.base.BaseActivity
import com.graduation.travelbook2.databinding.ActivityRegister2Binding

class SignUp2Activity2n1 : BaseActivity<ActivityRegister2Binding>() {
    override val TAG : String = SignUp2Activity2n1::class.java.simpleName
    override val layoutRes: Int = R.layout.activity_register2

    private val auth = FirebaseAuth.getInstance() // 유저 만들기
    private val firebaseUser: FirebaseUser? = auth.currentUser
    private val mDBRef = FirebaseDatabase.getInstance().reference.child("TravelBook2")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSignUpBtn()
    }

    private fun checkPwdCondition(): Boolean{
        binding.apply {
            if (TextUtils.isEmpty(etxPwd.text.toString())
                || TextUtils.isEmpty(etxPwdChk.text.toString())
                || etxPwd.text.toString().length < 6
                || etxPwdChk.text.toString().length < 6){
                Toast.makeText(this@SignUp2Activity2n1,
                    "비밀번호가 입력되지 않거나 6자 이하입니다.", Toast.LENGTH_SHORT).show()
                return false
            }else if (etxPwd.text.toString() != etxPwdChk.text.toString()){
                Toast.makeText(this@SignUp2Activity2n1,
                    "비밀번호와 비밀번호 확인에 입력한 값이 일치하지 않습니다.",
                    Toast.LENGTH_SHORT).show()
                return false
            }
            return true
        }
    }

    private fun setSignUpBtn() {
        binding.btnSignup.setOnClickListener{
            if (checkPwdCondition()){
                signUp()
            }
        }
    }

    private fun signUp() {
        val strEmail = intent.getStringExtra("strEmail")!!
        Log.i(TAG, strEmail)
        val strPwd = binding. etxPwd.text.toString()

        // Firebase Auth 진행
        auth.createUserWithEmailAndPassword(strEmail, strPwd)
            .addOnCompleteListener (this@SignUp2Activity2n1) { task ->
                if (task.isSuccessful) { // 가입성공시
                    if (firebaseUser != null) {
                        val account =
                            UserDto(firebaseUser.uid, firebaseUser.email, strPwd)

                        // db에 회원정보 insert
                        mDBRef.child("UserAccount").child(firebaseUser.uid)
                            .setValue(account)

                        Toast.makeText(
                            this@SignUp2Activity2n1,
                            "회원가입에 성공하셨습니다.",
                            Toast.LENGTH_SHORT
                        ).show()

                        // shared pref 로 앱 최초 실행 여부 기억
                        MyApplication.prefs.setString("isFirst", "true")

                        // 로그인 화면으로 이동
                        val lIntent =
                            Intent(this@SignUp2Activity2n1, LoginActivity3::class.java)
                        lIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(lIntent)
                        finish() // 현재 액티비티 파괴
                    }
                } else {
                    print(task.result)
                    Toast.makeText(
                        this@SignUp2Activity2n1,
                        "회원가입에 실패하셨습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}