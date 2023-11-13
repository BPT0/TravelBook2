package com.graduation.travelbook2.start

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
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
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale


/**
 * 스플레쉬 화면
 * 1.권한처리
 * 2.내부 저장소의 사진들 -> 지역 DB에 기록,
 * 2-1.pref 활용하여 기록은 1번만!, 기록 완료까지 다음 액티비티 실행막기
 * 3.지정된 액티비티이동
 * */
@SuppressLint("CustomSplashScreen")
class SplashActivity0 : BaseActivity<ActivitySplashBinding>() {

    override val TAG: String = SplashActivity0::class.java.simpleName
    override val layoutRes: Int = R.layout.activity_splash

    private lateinit var layout: View

    private val REQ_GALLERY_CODE = 1001

    private lateinit var dialogBuilder: AlertDialog.Builder
    private var dialog: AlertDialog? = null
    private var snackBar: Snackbar? = null

    private val galleryPermission = if (Build.VERSION.SDK_INT < 33) { // 33이하면
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_MEDIA_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    } else {
        arrayOf(
            // API33 이상 에서는 READ_MEDIA_IMAGES 권한이 READ_EXTERNAL_STORAGE 권한 대체
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.ACCESS_MEDIA_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }

    private lateinit var db: ImgInfoDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = binding.rootLayout

        db = ImgInfoDb.getInstance(this)!!
    }

    override fun onStart() {
        super.onStart()
        checkPermissions()
    }

    /*
    * 1.권한이 있는 경우 사진 정보 -> DB에 저장
    * 2. 권한이 없는 경우 권한 재요청
    * */
    private fun checkPermissions() {
        Log.d("요청된 권한들", galleryPermission.size.toString())
        if (!runtimeCheckPermission(this@SplashActivity0, *galleryPermission)) { // 권한 없을시 다시 요청
            Log.d("재요청할 권한들", galleryPermission.size.toString())
            // 권한 요청
            ActivityCompat.requestPermissions(
                this@SplashActivity0, galleryPermission, REQ_GALLERY_CODE
            )
        } else {
            Toast.makeText(this, "권한 허용됨", Toast.LENGTH_SHORT).show()
            CoroutineScope(Dispatchers.Main).launch {
                withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                    if (db.imgInfoDao().getAllImgInfo().isEmpty()) // db에 이미지 있는지 확인 후
                        loadRotateImages()
                }

                // 액티비티로 이동 로직 실행
                checkFirstRunAndLogined()
            }
        }
    }

    // 런타임 권한 체크
    private fun runtimeCheckPermission(context: Context?, vararg permissions: String): Boolean {
        if (context != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) { // 권한이 부여되지 않았다면 -> false -> 권한 재요청로직 진행
                    Log.e("$permission 권한체크여부", "체크안됨")
                    return false
                }
            }
            Log.e("$permissions 권한", "체크됨")
            return true
        }
        return true
    }

    //권한 요청에 대한 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQ_GALLERY_CODE -> {
                // 요청 권한이 비어있는 경우 에러
                if (grantResults.isEmpty()) {
                    Log.e("요청된 권한배열 비어있음", "Empty Permission Result")
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
                if (isPermitted) {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(
                            this@SplashActivity0,
                            "이미지 불러오기 권한을 허용했습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        withContext(CoroutineScope(Dispatchers.IO).coroutineContext){
                            if (db.imgInfoDao().getAllImgInfo().isEmpty()) {
                                loadRotateImages() // 이미지를 가져옴
                            }
                        }
                        checkFirstRunAndLogined()
                    }
                } else {
                    // 거부를 하나라도 선택한 경우 선택한 경우
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this@SplashActivity0, Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    ) {
                        // 권한이 필요하다는 토스트 메시지를 띄운다
                        Toast.makeText(
                            this@SplashActivity0,
                            "이미지 불러오기 기능을 사용하는데 해당 권한이 필요합니다",
                            Toast.LENGTH_SHORT
                        ).show()
                        // 거부된 권한이 있을시 해당 권한들을 다시 요청한다
                        requestPermissions(
                            deniedPermission.toArray(
                                arrayOfNulls<String>(deniedPermission.size)
                            ), 0
                        )
                    }
                    // 거부 및 다시보지 않기를 선택한 경우
                    else {
                        // 권한 설정으로 이동할 수 있도록 snackBar를 띄우고
                        Log.d("snackbar", "스낵바띄우기")
                        showDialogToGetPermission(this@SplashActivity0)
                    }
                }
            }
        }
    }

    // 직접 권한 설정을 하기 위한 알림창
    private fun showDialogToGetPermission(context: Context) {
        dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle("권한설정")
            .setMessage(
                "TravelBook의 사진을 불러오기 위한 기능이 허용 되지 않았습니다.\n" +
                        "확인을 눌러 권한 설정창으로 이동한 뒤 권한을 허용 해주세요."
            )
            .setPositiveButton("확인") { _, _ ->
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .setCancelable(false)
        dialogBuilder.setNegativeButton("거부") { _, _ ->
            snackBar = Snackbar.make(
                layout, "미디어 및 위치 접근 권한이 필요합니다.\n확인을 누르면 설정화면으로 이동합니다.",
                Snackbar.LENGTH_INDEFINITE
            )
            snackBar?.setAction("확인") {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            snackBar?.show()
        }

        dialog = dialogBuilder.create()
        dialog?.show()
    }

    override fun onStop() {
        super.onStop()
        if(dialog!=null) dialog?.dismiss()
        if(snackBar!=null) snackBar?.dismiss()
    }

    private suspend fun loadRotateImages(){
        Log.d("DB에 이미지 저장", "실행")
        // projection: 이미지 에서 불러올 정보 설정
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DISPLAY_NAME,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.DATA
        )

        // 기기의 MediaStore에 있는 데이터를 query문을 사용해 cusor 가져옴
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
            null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"
        )

        // todo: 인터넷 연결 여부 확인
        // cursor 값 = null (질의문의 정보가 null일때), DB에 데이터가 값이 없다면
        if (cursor != null && db.imgInfoDao().getAllImgInfo().isEmpty() && checkNetworkStatus()) {
            while (cursor.moveToNext()) {
                // 사진 경로 uri 가져오기
                val columIndex = cursor.getColumnIndexOrThrow(
                    MediaStore.Images.Media.DATA
                )
                var imagePath = cursor.getString(columIndex)

                // 사진 위치 정보 가져오기
                val exif = ExifInterface(imagePath)

                val gps = exif.latLong
                val date = exif.dateTime

                val rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                val rotationInDegrees = when (rotation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    else -> 0
                }

                // 이미지에 위치정보와 날짜 정보가 있다면
                if (gps != null && date != null) {
                    val locality = getLocalityFromCoordinates(gps[0], gps[1])

                    // 이미지의 위치정보에 기반해 지역명이 지정되었다면
                    if (locality.isNotEmpty()) {
                        Log.e(TAG, date.toString())

                        CoroutineScope(Dispatchers.IO).launch {

                            /*if(rotateAndSaveImage(File(imagePath), rotationInDegrees).isNotEmpty()){
                                CoroutineScope(Dispatchers.IO).async {
                                    imagePath = rotateAndSaveImage(File(imagePath), rotationInDegrees)
                                }.await()
                            }*/

                            // db에 이미지 추가
                            db.imgInfoDao().insertImgInfo(
                                ImgInfo(
                                    imagePath, gps[0], gps[1], locality, date,
                                    isChecked = false,
                                )
                            )
                        }
                    } else {
                        println("지역을 찾을 수 없습니다.")
                    }
                }
            }
            cursor.close()
            // todo: 커서가 닫혔다면 다음 진행
        }
    }


    private fun rotateAndSaveImage(sourceFile: File, rotationInDegrees: Int) : String{
        try {
            val originalBitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(sourceFile))

            val matrix = Matrix()
            matrix.postRotate(rotationInDegrees.toFloat())

            val rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)

            val outputDir = File(applicationContext.filesDir, "rotate_images")
            if(!outputDir.exists()){
                outputDir.mkdirs()
            }
            val outputFile = File(outputDir, "rotated_image$rotationInDegrees.jpg") // 새 이미지 파일

            val outputStream = FileOutputStream(outputFile)
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            return "$outputFile"

            // 새 이미지 파일이 저장되었습니다: outputFile
        } catch (e: IOException) {
            e.printStackTrace()
            return ""
        }
    }

    @SuppressLint("RestrictedApi")
    private fun loadImages() {
        Log.d("DB에 이미지 저장", "실행")
        // projection: 이미지 에서 불러올 정보 설정
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DISPLAY_NAME,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.DATA
        )

        // 기기의 MediaStore에 있는 데이터를 query문을 사용해 cusor 가져옴
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
            null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"
        )

        // todo: 인터넷 연결 여부 확인
        // cursor 값 = null (질의문의 정보가 null일때), DB에 데이터가 값이 없다면
        if (cursor != null && db.imgInfoDao().getAllImgInfo().isEmpty() && checkNetworkStatus()) {
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

                // 이미지에 위치정보와 날짜 정보가 있다면
                if (gps != null && date != null) {
                    val locality = getLocalityFromCoordinates(gps[0], gps[1])

                    // 이미지의 위치정보에 기반해 지역명이 지정되었다면
                    if (locality.isNotEmpty()) {
                        Log.e(TAG, date.toString())

                        CoroutineScope(Dispatchers.IO).launch {
                            // db에 이미지 추가
                            db.imgInfoDao().insertImgInfo(
                                ImgInfo(
                                    imagePath, gps[0], gps[1], locality, date,
                                    isChecked = false,
                                )
                            )
                        }
                    } else {
                        println("지역을 찾을 수 없습니다.")
                    }
                }
            }
            cursor.close()
            // todo: 커서가 닫혔다면 다음 진행
        }
    }

    // 네트워크 접속여부 체크
    // todo: 네트워크가 없으면 null포인트 오류 발생
    private fun checkNetworkStatus(): Boolean {
        return true
//        val cm = getSystemService(this.applicationContext.toString()) as ConnectivityManager
//        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
//
//        if (activeNetwork != null && activeNetwork.isAvailable) {
//            // 네트워크에 연결 중
//            Toast.makeText(this, "네트워크에 연결 되었습니다.", Toast.LENGTH_SHORT).show()
//            return true
//        } else {
//            // 네트워크에 연결되지 않음
//            Toast.makeText(this, "네트워크에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show()
//            return false
//        }
    }

    // 사진의 위치 정보를 기반으로 지역명을 리턴하는 함수
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
        } catch (e: Exception) {
            Log.d(e.toString(), e.toString())
        }

        return ""
    }

    // 권한 체크 여부와 로그인 여부에 따라 실행엑티비티를 결정하는 함수
    private fun checkFirstRunAndLogined() {
        if (MyApplication.prefs.getString("isFirst", "") == "true") {
            if (MyApplication.prefs.getString("isLogined", "") == "true") {
                val intent =
                    Intent(this, LoginActivity3::class.java) // 스플래시 화면 종료 후 표시할 메인 액티비티로 이동합니다.
                intent.putExtra("emailId", MyApplication.prefs.getString("strEmail", ""))
                intent.putExtra("password", MyApplication.prefs.getString("strPwd", ""))
                startActivity(intent)
                finish()
            }
        } else {
            val intent =
                Intent(this, Register1Activity1::class.java) // 스플래시 화면 종료 후 표시할 메인 액티비티로 이동합니다.
            startActivity(intent)
            finish()
        }
    }
}