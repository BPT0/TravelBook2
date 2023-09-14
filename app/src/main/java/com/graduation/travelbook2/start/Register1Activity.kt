package com.graduation.travelbook2.start

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.graduation.travelbook2.R
import com.graduation.travelbook2.base.BaseActivity
import com.graduation.travelbook2.databinding.ActivityRegister1Binding

class Register1Activity : BaseActivity<ActivityRegister1Binding>(){
    override val TAG : String = Register1Activity::class.java.simpleName
    override val layoutRes: Int = R.layout.activity_register1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mDBRef = FirebaseDatabase.getInstance().reference.child("TravelBook2")

        binding.apply {
            etxEmail.apply {

            }

            if (TextUtils.isEmpty(etxEmail.text.toString())) {
                // tvWarning.visibility = View.VISIBLE // 워닝 메시지 표시
            }

            btnComplete.apply {

                setOnClickListener {
                    // 회원가입 처리 시작
                    val strEmail = etxEmail.text.toString()

                    val query = mDBRef.child("UserAccount")
                        .orderByChild("emailId").equalTo(strEmail)


                    query.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val emailsExist = snapshot.exists()
                            if(emailsExist) {
                                Toast.makeText(this@Register1Activity, "이미 존재하는 이메일입니다",Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@Register1Activity, RegisterLoginActivity::class.java)
                                intent.putExtra("strEmail", strEmail)
                                startActivity(intent)
                                finish()
                            }else{
                                val intent = Intent(this@Register1Activity, Register2Activity::class.java)
                                intent.putExtra("strEmail", strEmail)
                                startActivity(intent)
                                finish()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })

                }
            }
        }
    }
}