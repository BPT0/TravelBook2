package com.graduation.travelbook2.start

import android.content.Intent
import android.os.Bundle
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

class Register2Activity : BaseActivity<ActivityRegister2Binding>() {
    override val TAG : String = Register2Activity::class.java.simpleName
    override val layoutRes: Int = R.layout.activity_register2

    val auth = FirebaseAuth.getInstance() // 유저 만들기

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val firebaseUser: FirebaseUser? = auth.currentUser
        val mDBRef = FirebaseDatabase.getInstance().reference.child("TravelBook2")

        binding.apply {

            /*if (TextUtils.isEmpty(etxPwd.text.toString())) {
                // binding.warning.visibility = View.VISIBLE // 워닝 메시지 표시
            }*/

            /*if (etxPwd.text.toString().length < 6) {
                // 워닝 메시지 표시
                Toast.makeText(this@Register1Activity, "비밀번호는 6자리 이상이어야 합니다..", Toast.LENGTH_SHORT).show()
            }*/

            // todo. 2개 택스트가 일치하지 않다는 워닝 메시지 표시

            btnComplete.apply {
                setOnClickListener{
                    val strEmail = intent.getStringExtra("strEmail")!!
                    Log.i(TAG, strEmail)
                    val strPwd = etxPwd.text.toString()

                    // Firebase Auth 진행
                    auth.createUserWithEmailAndPassword(strEmail, strPwd)
                        .addOnCompleteListener (this@Register2Activity) { task ->
                        if (task.isSuccessful) { // 가입성공시
                            if (firebaseUser != null) {
                                val account =
                                    UserDto(firebaseUser.uid, firebaseUser.email, strPwd)

                                // db에 회원정보 insert
                                mDBRef.child("UserAccount").child(firebaseUser.uid)
                                    .setValue(account)

                                Toast.makeText(
                                    this@Register2Activity,
                                    "회원가입에 성공하셨습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // shared pref 로 앱 최초 실행 여부 기억
                                MyApplication.prefs.setString("isFirst", "true")

                                // 로그인 화면으로 이동
                                val lIntent =
                                    Intent(this@Register2Activity, LoginActivity::class.java)
                                lIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(lIntent)
                                finish() // 현재 액티비티 파괴
                            }
                        } else {
                            print(task.result)
                            Toast.makeText(
                                this@Register2Activity,
                                "회원가입에 실패하셨습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

        }
    }
}