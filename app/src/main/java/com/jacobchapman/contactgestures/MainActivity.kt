package com.jacobchapman.contactgestures

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.gesture.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
import android.graphics.Bitmap
import android.os.Environment
import java.io.File.separator
import android.os.Environment.getExternalStorageDirectory
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.view.drawToBitmap
import androidx.fragment.app.DialogFragment
import java.io.File
import java.io.FileOutputStream
import java.util.jar.Manifest


class MainActivity : AppCompatActivity(){

//    private val gestureOverlay by lazy { gesture_overlay }
    private lateinit var gestureLibrary: GestureLibrary
    private var gesture: Gesture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        openOptionsMenu()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

//        gestureLibrary = GestureLibraries.fromFile("/sdcard/gestures.txt")
//        if(!gestureLibrary.load()) {
//            finish()
//        }
//        gestureOverlay.gestureStrokeType = GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE
//        gestureOverlay.addOnGestureListener(this)
//        gestureOverlay.addOnGesturePerformedListener { overlay, gesture ->
//            val predictions = gestureLibrary.recognize(gesture)
//            val topPrediction = predictions.maxBy { it.score }
//            topPrediction?.let {
//                Toast.makeText(this, topPrediction.name, Toast.LENGTH_LONG).show()
//            }
//        }
    }

}

interface SaveDialogListener {
    fun onSaveDialogFinished(fileName: String?)
}

class SaveDialogFragment(private val saveDialogListener: SaveDialogListener) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(context!!)
        val input = EditText(activity)
        input.inputType = InputType.TYPE_CLASS_TEXT
        dialogBuilder.setView(input)
        dialogBuilder.setPositiveButton("Save") { _, _ ->
            saveDialogListener.onSaveDialogFinished(input.text.toString())
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            saveDialogListener.onSaveDialogFinished(null)
            dialog.cancel()
        }
        return dialogBuilder.create()
    }
}