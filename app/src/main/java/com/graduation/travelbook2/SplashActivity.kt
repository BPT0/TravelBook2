package com.graduation.travelbook2

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.exifinterface.media.ExifInterface
import com.google.android.material.snackbar.Snackbar
import com.graduation.travelbook2.base.BaseActivity
import com.graduation.travelbook2.databinding.ActivitySplashBinding
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.database.ImgInfoDb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.activityManager
import java.lang.Exception
import java.util.Locale
import kotlin.concurrent.thread

/**
 * 스플레쉬 화면: 권한처리 내부 저장된 사진 다운로드후 액티비티이동
 * */
@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    override val TAG : String = SplashActivity::class.java.simpleName
    override val layoutRes: Int = R.layout.activity_splash

    companion object {
        // 갤러리 권한 요청번호
        const val REQ_GALLERY = 1001
        const val TAG = "SplashActivity"
    }

    private lateinit var layout: View

    private val REQ_GALLERY = 1001

    val GALLERY_PERMISSIONS = if(Build.VERSION.SDK_INT < 33){ // 33이하면
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
    } else {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            // API33 이상 에서는 READ_MEDIA_IMAGES 가 READ_EXTERNAL_STORAGE 위 권한을 대신함
        )
    }

    private lateinit var db : ImgInfoDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = binding.rootLayout

        db = ImgInfoDb.getInstance(this)!!

    }

    override fun onStop() {
        super.onStop()
        // todo: 권한 확인 작업 종료하기
    }

    /**
     * paused 후 다시 시작시 권한 체크하기
     */
    override fun onRestart() {
        super.onRestart()
    }

    override fun onStart() {
        super.onStart()
        checkPermission()
    }

    private var totalpages= 0

    private fun loadImages(){
        Log.d("loadImage 실행", "실행됨")
        // projection: 이미지 에서 불러올 정보 설정
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DISPLAY_NAME,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.DATA
        )

        // 애플리케이션 패키지에 db 질의
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
            null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"
        )

        totalpages = cursor!!.count
        println("총 사진수 $totalpages")

        if (cursor!=null) {
            thread() {
                if(db.imgInfoDao().getAllImgInfo().isEmpty()) { // DB에 데이터가 값이 없다면
                    runOnUiThread { binding.progressCircular.visibility = View.VISIBLE }
                    while (cursor.moveToNext()) {
                        // 사진 경로 uri 가져오기
                        val columIndex = cursor.getColumnIndexOrThrow(
                            MediaStore.Images.Media.DATA
                        )
                        val imagePath = cursor.getString(columIndex)

                        // 사진 위치 정보 가져오기
                        val exif = ExifInterface(imagePath)
                        val gps = exif.latLong
                        val date = exif.getAttribute(ExifInterface.TAG_DATETIME)

                        // 이미지에 위치정보와 날씨 정보가 있다면
                        if (gps != null && date != null) {
                            val locality = getLocalityFromCoordinates(gps[0], gps[1])

                            // 이미지에 지역명이 비어있지 않다면
                            if (locality.isNotEmpty()) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    db.imgInfoDao().insertImgInfo(
                                        ImgInfo(
                                            imagePath, gps[0], gps[1], locality, date, isChecked = false
                                        )
                                    )
                                }
                            } else println("지역을 찾을 수 없습니다.")
                        }
                    }
                    cursor.close()
                    val intent = Intent(this, MainActivity::class.java) // 스플래시 화면 종료 후 표시할 메인 액티비티로 이동합니다.
                    startActivity(intent)
                    finish()
                }else{
                    val intent = Intent(this, MainActivity::class.java) // 스플래시 화면 종료 후 표시할 메인 액티비티로 이동합니다.
                    startActivity(intent)
                    finish()
                }

            }
        }

    }

    private fun getLocalityFromCoordinates(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())

        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 3)
            addresses!!.let {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    return address.locality ?: ""
                }
            }
        }catch (e: Exception){
            Log.d(e.toString(), e.toString())
        }

        return ""
    }

    private fun checkPermission(){
        Log.d(TAG, "권한요청")
        Log.d(TAG, GALLERY_PERMISSIONS.size.toString())
        if(!runtimeCheckPermission(this, *GALLERY_PERMISSIONS)){
            // 권한 없을시 다시 요청
            Log.d(TAG, "권한 재요청")
            ActivityCompat.requestPermissions(this,
                GALLERY_PERMISSIONS, REQ_GALLERY
            )
        }else{
            Log.d(TAG, "권한 허용됨")
            // 권한이 있는 경우 사진 정보 다운
            //  완료시 메인 액티비티로 이동
            Toast.makeText(this, "이미지 불러오기 권한을 허용했습니다.", Toast.LENGTH_SHORT).show()
            CoroutineScope(Dispatchers.IO).launch {
                loadImages() // 이미지를 가져옴
            }
        }
    }

    // 권한 런타임 체크하는 함수
    private fun runtimeCheckPermission(context: Context?, vararg permissions: String?): Boolean {
        if (context != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission!!) != PackageManager.PERMISSION_GRANTED)
                    return false
            }
        }
        return true
    }

    //권한 요청에 대한 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // todo: callback 방식으로 변경
        when(requestCode) {
            REQ_GALLERY -> {
                // 요청 권한이 비어있는 경우 에러
                if (grantResults.isEmpty()) {
                    throw RuntimeException("Empty Permission Result")
                }
                // 거부된 권한이 있는지 확인한다
                var isPermitted = true
                val deniedPermission = ArrayList<String>() // 거부된 권한들
                for ((id, result) in grantResults.withIndex()) {
                    if (result == PackageManager.PERMISSION_DENIED) {
                        isPermitted = false
                        deniedPermission.add(permissions[id])
                    }
                }
                // 권한이 모두 충족된 경우 다이얼로그를 보여주고 메인 액티비티로 이동
                if (isPermitted) {
                    Toast.makeText(this, "이미지 불러오기 권한을 허용했습니다.", Toast.LENGTH_SHORT).show()
                    CoroutineScope(Dispatchers.IO).launch {
                        if(db.imgInfoDao().getAllImgInfo().isEmpty()){ // DB에 데이터가 값이 없다면
                            loadImages() // 이미지를 가져옴
                        }
                    }
                } else {
                    // 거부를 하나라도 선택한 경우 선택한 경우
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    ) {
                        // 권한이 필요하다는 토스트 메시지를 띄운다
                        Toast.makeText(this, "이미지 불러오기 기능을 사용하는데 해당 권한이 필요합니다", Toast.LENGTH_SHORT).show()
                        // 권한을 다시 요청한다
                        requestPermissions(
                            deniedPermission.toArray(
                                arrayOfNulls<String>(
                                    deniedPermission.size
                                )
                            ), 0
                        )
                    }
                    // 거부 및 다시보지 않기를 선택한 경우
                    else {
                        // 권한 설정으로 이동할 수 있도록 알림창을 띄우고
                        showDialogToGetPermission(this)

                    }
                }
            }
        }
    }

    // 직접 권한 설정을 하기 위한 알림창
    private fun showDialogToGetPermission(context: Context){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("권한설정")
            .setMessage("TravelBook의 사진 접근 기능을 사용하기 위해 외부 스토리지 접근 권한이 필요합니다." +
                    "확인을 눌러 권한 설정창으로 이동한 뒤 설정을 완료해주세요")
            .setPositiveButton("확인"){ _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .setCancelable(false)
        builder.setNegativeButton("거부"){ _, _ ->
            val snackBar = Snackbar.make(layout, "외부 저장소 접근권한이 필요합니다.\n확인을 누르면 설정화면으로 이동합니다.",
                Snackbar.LENGTH_INDEFINITE
            )
            snackBar.setAction("확인") {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            snackBar.show()
        }
        val dialog = builder.create()

        // todo: 알림창 중복으로 띄우지 않게하기
        /*if (dialog != null && dialog.isShowing) {
            // AlertDialog가 활성화되어 있음
        } else {
            // AlertDialog가 비활성화되어 있음
            dialog.show()
        }*/
    }


}