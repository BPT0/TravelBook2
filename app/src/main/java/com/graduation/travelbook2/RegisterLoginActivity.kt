package com.graduation.travelbook2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.graduation.travelbook2.base.BaseActivity
import com.graduation.travelbook2.databinding.ActivityRegisterLoginBinding
import com.graduation.travelbook2.sharedpref.MyApplication

class RegisterLoginActivity : BaseActivity<ActivityRegisterLoginBinding>() {
    override val TAG : String = RegisterLoginActivity::class.java.simpleName
    override val layoutRes: Int = R.layout.activity_register_login

    private var auth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        binding.apply {

            etxPwd.apply {
                // 키보드가 입력될 때
                // if (etxPwd.text.toString().length < 6) {
                //                // 워닝 메시지 표시
                //                Toast.makeText(this@RegisterActivity, "비밀번호는 6자리 이상이어야 합니다..", Toast.LENGTH_SHORT).show()
                //            }
            }

            if (TextUtils.isEmpty(etxPwd.text.toString())) {
                // binding.warning.visibility = View.VISIBLE // 워닝 메시지 표시
            }

            // Firebase Auth 진행
            btnLogin.apply{
                setOnClickListener {
                    // 로그인 처리 시작
                    val strEmail = intent.getStringExtra("strEmail")!!
                    val strPwd = etxPwd.text.toString()

                    // Firebase Auth 진행
                    Log.i(TAG, "$strEmail $strPwd")
                    auth?.signInWithEmailAndPassword(strEmail, strPwd)?.addOnCompleteListener(this@RegisterLoginActivity) {task ->
                        if (task.isSuccessful){ // 로그인 성공시
                            Toast.makeText(this@RegisterLoginActivity, "로그인에 성공하셨습니다.", Toast.LENGTH_SHORT).show()

                            // shared pref 로 앱 최초 실행 여부 기억
                            MyApplication.prefs.setString("isFirst", "true")

                            val lIntent = Intent(this@RegisterLoginActivity, MainActivity::class.java)
                            lIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(lIntent)
                        } else{
                            Log.e(TAG, task.exception.toString())
                            Toast.makeText(this@RegisterLoginActivity, "로그인에 실패하셨습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}