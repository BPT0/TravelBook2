package com.graduation.travelbook2.start

import android.Manifest
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
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.exifinterface.media.ExifInterface
import com.google.android.material.snackbar.Snackbar
import com.graduation.travelbook2.MyApplication
import com.graduation.travelbook2.R
import com.graduation.travelbook2.base.BaseActivity
import com.graduation.travelbook2.database.ImgInfo
import com.graduation.travelbook2.database.ImgInfoDb
import com.graduation.travelbook2.databinding.ActivitySplashBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale


/**
 * 스플레쉬 화면: 권한처리 내부 저장된 사진 지역 DB에 다운로드후 지정된 액티비티이동
 * */
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    override val TAG : String = SplashActivity::class.java.simpleName
    override val layoutRes: Int = R.layout.activity_splash

    private lateinit var layout: View

    private val REQ_GALLERY_CODE = 1001

    private lateinit var dialogBuilder: AlertDialog.Builder
    private lateinit var dialog : AlertDialog
    private lateinit var snackBar: Snackbar

    @RequiresApi(Build.VERSION_CODES.Q)
    val GALLERY_PERMISSIONS = if(Build.VERSION.SDK_INT < 33){ // 33이하면
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_MEDIA_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    } else {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            // API33 이상 에서는 READ_MEDIA_IMAGES 가 READ_EXTERNAL_STORAGE 위 권한을 대신함
            Manifest.permission.ACCESS_MEDIA_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_MEDIA_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    }

    private lateinit var db : ImgInfoDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = binding.rootLayout

        db = ImgInfoDb.getInstance(this)!!

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStart() {
        super.onStart()
        if(checkPermissions()){
            // 권한이 있는 경우 사진 정보 다운
            //  완료시 진행 확인후 액티비티로 이동
            CoroutineScope(Dispatchers.Main).launch {
                CoroutineScope(Dispatchers.IO).async {
                    if(db.imgInfoDao().getAllImgInfo().isEmpty())
                        loadImages() // 이미지를 가져옴
                }.await()
                println("서브 스레드 작업 완료")
                Log.i(TAG, "자동 로그인 로직 실행")
                checkFirstRunAndLogined()
            }
        }
    }

    private fun loadImages(){
        Log.d("loadImage 실행", "실행됨")
        // projection: 이미지 에서 불러올 정보 설정
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DISPLAY_NAME,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.DATA
        )

        // 기기의 MediaStore에 있는 데이터를 질의문을 사용해 cusor 가져옴
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
            null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"
        )

        if (cursor!=null) {
            if(db.imgInfoDao().getAllImgInfo().isEmpty()) { // DB에 데이터가 값이 없다면

                while (cursor.moveToNext()) {
                    // 사진 경로 uri 가져오기
                    val columIndex = cursor.getColumnIndexOrThrow(
                        MediaStore.Images.Media.DATA
                    )
                    val imagePath = cursor.getString(columIndex)

                    // 사진 위치 정보 가져오기
                    val exif = ExifInterface(imagePath)
                    val gps = exif.latLong
                    val date = exif.dateTime

                    // 이미지에 위치정보와 날씨 정보가 있다면
                    if (gps != null && date != null) {
                        val locality = getLocalityFromCoordinates(gps[0], gps[1])

                        // 이미지에 지역명이 비어있지 않다면
                        if (locality.isNotEmpty()) {
                            Log.e(TAG, date.toString())

                            getOrientationOfImage(imagePath)
                            CoroutineScope(Dispatchers.IO).launch {
                                // db에 이미지 추가
                                db.imgInfoDao().insertImgInfo(
                                    ImgInfo(
                                        imagePath, gps[0], gps[1], locality, date,
                                        isChecked = false,
                                    )
                                )
                            }
                        } else println("지역을 찾을 수 없습니다.")
                    }
                }
                cursor.close()
            }
        }
    }

    private fun getOrientationOfImage(imagePath: String) : Int{
        /**
         * Exif 태그를 JPEG 파일 또는 RAW 이미지 파일로 읽고 쓰는 클래스
         * 즉, 이미지가 갖고 있는 정보의 집합 클래스
         */
        var exif: ExifInterface? = null

        try {
            exif = ExifInterface(imagePath) // exif 객체에 사진경로 할당
        } catch (e: IOException) {
            return -1 // 음수는 false, 양수는 true(표준 약속)
        }
        // getAttributeInt = 지정된 태그의 정수 값을 반환(String tag, int defaultValue)
        // orientation 으로 지정된 정수 값을 할당
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1)
        // 사진의 회전에 맞게 바르게 설정
        if (orientation != -1) {
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> return 90
                ExifInterface.ORIENTATION_ROTATE_180 -> return 180
                ExifInterface.ORIENTATION_ROTATE_270 -> return 270
            }
        }
        return 0 // 회전 성공
    }

    private fun checkFirstRunAndLogined(){
        if(MyApplication.prefs.getString("isFirst", "") == "true"){
            if (MyApplication.prefs.getString("isLogined", "") == "true"){
                val intent = Intent(this, LoginActivity::class.java) // 스플래시 화면 종료 후 표시할 메인 액티비티로 이동합니다.
                intent.putExtra("emailId", MyApplication.prefs.getString("strEmail", ""))
                intent.putExtra("password", MyApplication.prefs.getString("strPwd", ""))
                startActivity(intent)
                finish()
            }
        }else{
            val intent = Intent(this, Register1Activity::class.java) // 스플래시 화면 종료 후 표시할 메인 액티비티로 이동합니다.
            startActivity(intent)
            finish()
        }
    }

    private fun getLocalityFromCoordinates(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())

        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 5)
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

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkPermissions(): Boolean{
        Log.d(TAG, "권한요청")
        Log.d(TAG, GALLERY_PERMISSIONS.size.toString())

        if(!runtimeCheckPermission(this, *GALLERY_PERMISSIONS)){
            // 권한 없을시 다시 요청
            Log.d(TAG, "권한 재요청")
            Log.d("GALLERY_PERMISSIONS", GALLERY_PERMISSIONS.size.toString())
            ActivityCompat.requestPermissions(this, GALLERY_PERMISSIONS, REQ_GALLERY_CODE)
            return false
        }
        Log.d(TAG, "권한 허용됨")
        Toast.makeText(this, "이미지 불러오기 권한을 허용했습니다.", Toast.LENGTH_SHORT).show()
        return true

    }

    // 권한 런타임 체크하는 함수
    private fun runtimeCheckPermission(context: Context?, vararg permissions: String?): Boolean {
        if (context != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission!!
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.e("권한체크", "체크안됨")
                    return false
                }
            }
            Log.e("권한체크", "체크됨")
            return true
        }
        return false
    }

    //권한 요청에 대한 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // todo: callback 방식으로 변경
        when(requestCode) {
            REQ_GALLERY_CODE -> {
                // 요청 권한이 비어있는 경우 에러
                if (grantResults.isEmpty()) {
                    Log.e("요청된 권한배열 비어있음","Empty Permission Result")
                    return
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
                GlobalScope.launch {
                    if (isPermitted) {
                        CoroutineScope(Dispatchers.Main).launch{
                            Toast.makeText(this@SplashActivity, "이미지 불러오기 권한을 허용했습니다.", Toast.LENGTH_SHORT).show()
                            async(Dispatchers.IO) {
                                if(db.imgInfoDao().getAllImgInfo().isEmpty()) // DB에 데이터가 값이 없다면
                                    loadImages() // 이미지를 가져옴
                            }
                            checkFirstRunAndLogined()
                        }
                    } else {
                        // 거부를 하나라도 선택한 경우 선택한 경우
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this@SplashActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                        ) {
                            // 권한이 필요하다는 토스트 메시지를 띄운다
                            Toast.makeText(this@SplashActivity, "이미지 불러오기 기능을 사용하는데 해당 권한이 필요합니다", Toast.LENGTH_SHORT).show()
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
                            // 권한 설정으로 이동할 수 있도록 snackBar를 띄우고
                            Log.d("snackbar", "스낵바띄우기")
                            showDialogToGetPermission(this@SplashActivity)
                        }
                    }
                }

            }
        }
    }

    // 직접 권한 설정을 하기 위한 알림창
    private fun showDialogToGetPermission(context: Context){
        dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle("권한설정")
            .setMessage("TravelBook의 사진을 불러오기 위한 기능이 허용 되지 않았습니다.\n" +
                    "확인을 눌러 권한 설정창으로 이동한 뒤 권한을 허용 해주세요.")
            .setPositiveButton("확인"){ _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .setCancelable(false)
        dialogBuilder.setNegativeButton("거부"){ _, _ ->
            snackBar = Snackbar.make(layout, "미디어 및 위치 접근 권한이 필요합니다.\n확인을 누르면 설정화면으로 이동합니다.",
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

        dialog = dialogBuilder.create()
        dialog.show()

    }
}