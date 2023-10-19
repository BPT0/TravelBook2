package com.graduation.travelbook2

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import ja.burhanrashid52.photoeditor.OnSaveBitmap
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import ja.burhanrashid52.photoeditor.ViewType


class PhotoEditorSampleActivity : AppCompatActivity() {

    private lateinit var mPhotoEditorView : PhotoEditorView
    private lateinit var mPhotoEditor : PhotoEditor
    private lateinit var btnAddText : Button
    private lateinit var etxAddText : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_editor_sample)

        mPhotoEditorView = findViewById(R.id.photoEditorView)
        btnAddText = findViewById(R.id.btn_add_text)
        etxAddText = findViewById(R.id.etx_add_text)

        mPhotoEditor = PhotoEditor.Builder(this, mPhotoEditorView)
            .setPinchTextScalable(true)
            .setClipSourceImage(true)
            .build()

        btnAddText.setOnClickListener {
            mPhotoEditor.addText(etxAddText.text.toString(), Color.BLACK)
        }

        mPhotoEditor.saveAsBitmap(object : OnSaveBitmap {
            override fun onBitmapReady(saveBitmap: Bitmap?) {
                Log.e("PhotoEditor", "Image Saved Successfully")

            }

            override fun onFailure(e: Exception?) {
                Log.e("PhotoEditor", "Failed to save Image")

            }
        })

        mPhotoEditor.setOnPhotoEditorListener(object: OnPhotoEditorListener{
            override fun onAddViewListener(viewType: ViewType?, numberOfAddedViews: Int) {
            }

            override fun onEditTextChangeListener(rootView: View?, text: String?, colorCode: Int) {

            }

            override fun onRemoveViewListener(viewType: ViewType?, numberOfAddedViews: Int) {
            }

            override fun onStartViewChangeListener(viewType: ViewType?) {
            }

            override fun onStopViewChangeListener(viewType: ViewType?) {
            }

            override fun onTouchSourceImage(event: MotionEvent?) {
            }

        })

    }
}
