package com.graduation.travelbook2.start

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.graduation.travelbook2.R
import com.graduation.travelbook2.base.BaseActivity
import com.graduation.travelbook2.databinding.ActivityRegister1Binding

class Register1Activity1 : BaseActivity<ActivityRegister1Binding>(){
    override val TAG : String = Register1Activity1::class.java.simpleName
    override val layoutRes: Int = R.layout.activity_register1

    private val mDBRef = FirebaseDatabase.getInstance().reference.child("TravelBook2")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCompleteBtn()
    }

    private fun setCompleteBtn() {
        binding.btnComplete.apply {
            setOnClickListener {
                if (TextUtils.isEmpty(binding.etxEmail.text.toString())) {
                    // 워닝 메시지 표시
                    Toast.makeText(
                        this@Register1Activity1,
                        "이메일이 입력되지 않았습니다...", Toast.LENGTH_SHORT
                    ).show()
                }else{
                    val strEmail = binding.etxEmail.text.toString()
                    val query = mDBRef.child("UserAccount")
                        .orderByChild("emailId").equalTo(strEmail)

                    query.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val emailsExist = snapshot.exists()
                            if(emailsExist) {
                                Toast.makeText(this@Register1Activity1, "이미 존재하는 이메일입니다",Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@Register1Activity1, SignUpLoginActivity2n2::class.java)
                                intent.putExtra("strEmail", strEmail)
                                startActivity(intent)
                                finish()
                            }else{
                                val intent = Intent(this@Register1Activity1, SignUp2Activity2n1::class.java)
                                intent.putExtra("strEmail", strEmail)
                                startActivity(intent)
                                finish()
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Log.e("dbError", error.toString())
                        }

                    })
                }

            }
        }
    }
}
