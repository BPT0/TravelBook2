package com.graduation.travelbook2

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.graduation.travelbook2.base.BaseActivity
import com.graduation.travelbook2.databinding.ActivityMainBinding
import com.graduation.travelbook2.search.SearchFragment
import com.graduation.travelbook2.myDiary.DiaryFragment
import com.graduation.travelbook2.share.ShareFragment


class MainActivity : BaseActivity<ActivityMainBinding>(), NavigationBarView.OnItemSelectedListener{

    override val TAG : String = MainActivity::class.java.simpleName
    override val layoutRes: Int = R.layout.activity_main

    // fragment 객체 선언
    private var shareFragment: ShareFragment? = null
    private var diaryFragment: DiaryFragment? = null
    private var searchFragment: SearchFragment? = null

    private lateinit var sentActivity: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            initBnv()
        }
        if(sentActivity() == "EditImgActivity"){
            binding.bnv.selectedItemId = R.id.share
        }


    }

    private fun sentActivity() : String {
        sentActivity = if(intent.getStringExtra("sentActivity")!= null) {
            intent.getStringExtra("sentActivity")!!
        }else{
            "isNotSentActivity"
        }
        return sentActivity
    }

    private fun initBnv() {
        binding.apply {
            bnv.disableTooltip()
            bnv.setOnItemSelectedListener(this@MainActivity)
            // 처음 표시되는 bnv 설정
            shareFragment = ShareFragment.newInstance()
            supportFragmentManager.beginTransaction().replace(
                R.id.main_frame, shareFragment!!).commit()
            bnv.selectedItemId = R.id.share
        }
    }

    private fun BottomNavigationView.disableTooltip() {
        val content: View = getChildAt(0)

        if (content is ViewGroup) {
            content.forEach {
                it.setOnLongClickListener {
                    return@setOnLongClickListener true
                }
                // disable vibration also
                it.isHapticFeedbackEnabled = false
            }
        }
    }

    // 클릭에 따라 바텀 네비게이션 변화 설정
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.share ->{
                item.setIcon(R.drawable.ic_share_blue)
                binding.bnv.apply {
                    menu.findItem(R.id.search).setIcon(R.drawable.ic_search_gray)
                    menu.findItem(R.id.book).setIcon(R.drawable.ic_book_gray)
                }
                if(shareFragment==null){
                    shareFragment = ShareFragment.newInstance()
                    supportFragmentManager.beginTransaction().add(
                        R.id.main_frame, shareFragment!!).commit()
                }else{
                    supportFragmentManager.beginTransaction().show(shareFragment!!).commit()
                }
                if(diaryFragment!=null)
                    supportFragmentManager.beginTransaction().hide(diaryFragment!!).commit()
                if(searchFragment!=null)
                    supportFragmentManager.beginTransaction().hide(searchFragment!!).commit()
            }
            R.id.search ->{
                item.setIcon(R.drawable.ic_search_blue)
                binding.bnv.apply {
                    menu.findItem(R.id.book).setIcon(R.drawable.ic_book_gray)
                    // menu.findItem(R.id.share).setIcon(R.drawable.ic_share_gray)
                }
                if(searchFragment==null){
                    searchFragment = SearchFragment.newInstance()
                    supportFragmentManager.beginTransaction().add(
                        R.id.main_frame, searchFragment!!).commit()
                }else{
                    supportFragmentManager.beginTransaction().show(searchFragment!!).commit()
                }
                if(diaryFragment!=null)
                    supportFragmentManager.beginTransaction().hide(diaryFragment!!).commit()
                if(shareFragment!=null)
                    supportFragmentManager.beginTransaction().hide(shareFragment!!).commit()
            }
            R.id.book ->{
                item.setIcon(R.drawable.ic_book_blue)
                binding.bnv.apply {
                    // menu.findItem(R.id.share).setIcon(R.drawable.ic_share_gray)
                    menu.findItem(R.id.search).setIcon(R.drawable.ic_search_gray)
                }
                if(diaryFragment==null) {
                    diaryFragment = DiaryFragment.newInstance()
                    supportFragmentManager.beginTransaction().add(
                        R.id.main_frame, diaryFragment!!
                    ).commit()
                }else{
                    supportFragmentManager.beginTransaction().show(diaryFragment!!).commit()
                }
                if(shareFragment!=null)
                    supportFragmentManager.beginTransaction().hide(shareFragment!!).commit()
                if(searchFragment!=null)
                    supportFragmentManager.beginTransaction().hide(searchFragment!!).commit()
            }
        }
        return true
    }

}