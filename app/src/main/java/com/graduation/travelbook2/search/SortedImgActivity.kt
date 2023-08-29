/*
package com.pipecodingclub.travelbook.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.pipecodingclub.travelbook.databinding.ActivityLocalGalleryBinding
import com.pipecodingclub.travelbook.search.adapter.AlbumAdapter
import com.pipecodingclub.travelbook.search.dto.ImgVO
import com.pipecodingclub.travelbook.search.adapter.SelAlbumAdapter

class SortedImgActivity : AppCompatActivity(){
    // 바인딩 변수 설정
    private var glBinding: ActivityLocalGalleryBinding? = null
    val binding get() = glBinding!!

    lateinit var  gAdapter : AlbumAdapter
    private lateinit var listImgVO: ArrayList<ImgVO>

    companion object{
        val selImageList : ArrayList<ImgVO> = arrayListOf(
            ImgVO("",
            false, doubleArrayOf(0.1, 0.1), "", ""
            )
        )
        var selImgAdapter : SelAlbumAdapter = SelAlbumAdapter(selImageList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        glBinding = ActivityLocalGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val glIntent = intent
        listImgVO = glIntent.getSerializableExtra("listImgVO") as ArrayList<ImgVO>

        Log.d("지역별 이미지 리스트", listImgVO.toString())
        binding.apply {
            // 갤러리 rv에 해당 값 불러오기
            rvPicture.apply {
                val gridLayoutManager = GridLayoutManager(context, 3)
                layoutManager = gridLayoutManager
                setHasFixedSize(true)
                gAdapter = AlbumAdapter(listImgVO, this@SortedImgActivity)
                adapter = gAdapter
            }

            // 선택된 사진만을 따로 rv에 값 표시하기
            rvPicturesUse.apply {
                val hLinearLayoutManager = LinearLayoutManager(context,
                    LinearLayoutManager.HORIZONTAL, false)
                layoutManager = hLinearLayoutManager
                setHasFixedSize(true)

                adapter = selImgAdapter
            }

            btnMkBook.setOnClickListener {
                Log.d("d", selImageList.toString())
                selImgAdapter = SelAlbumAdapter(selImageList)
                selImgAdapter.notifyDataSetChanged()
            }


        }
    }

    override fun onDestroy() {
        glBinding = null
        super.onDestroy()
    }
}*/
