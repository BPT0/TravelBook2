package com.graduation.travelbook2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.graduation.travelbook2.base.BaseActivity
import com.graduation.travelbook2.databinding.ActivityRegisterBinding

class RegisterActivity : BaseActivity<ActivityRegisterBinding>(){
    override val TAG : String = RegisterActivity::class.java.simpleName
    override val layoutRes: Int = R.layout.activity_register

    val auth = FirebaseAuth.getInstance() // 유저 만들기

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firebaseUser: FirebaseUser? = auth.currentUser
        val mDatabasRef = FirebaseDatabase.getInstance().reference.child("TravelBook2")

        binding.apply {
            etxEmail.apply {

            }

            etxPwd.apply {

            }

            if (TextUtils.isEmpty(etxEmail.text.toString()) || TextUtils.isEmpty(etxPwd.text.toString())) {
                // binding.warning.visibility = View.VISIBLE // 워닝 메시지 표시
            }
            if (etxPwd.text.toString().length < 6) {
                // 워닝 메시지 표시
                Toast.makeText(this@RegisterActivity, "비밀번호는 6자리 이상이어야 합니다..", Toast.LENGTH_SHORT).show()
            }

            btnRegister.apply {

                setOnClickListener {
                    // 회원가입 처리 시작
                    var strEmail = etxEmail.text.toString()
                    var strPwd = etxPwd.text.toString()

                    // Firebase Auth 진행
                    auth.createUserWithEmailAndPassword(strEmail, strPwd)
                        .addOnCompleteListener {task ->

                        if (task.isSuccessful){ // 가입성공시
                            // todo: db 규칙 설정
                            if (firebaseUser != null){
                                val account = UserDto(firebaseUser.uid, firebaseUser.email, strPwd)

                                // db에 회원정보 insert
                                mDatabasRef.child("UserAccount").child(firebaseUser.uid).setValue(account)

                                Toast.makeText(this@RegisterActivity, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show()

                                val lIntent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                lIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(lIntent)
                                finish() // 현재 액티비티 파괴
                            }
                        } else{
                            print(task.result)
                            Toast.makeText(this@RegisterActivity, "회원가입에 실패하셨습니다.", Toast.LENGTH_SHORT).show()
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