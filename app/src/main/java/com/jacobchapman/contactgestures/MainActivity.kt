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


class MainActivity : AppCompatActivity(), GestureOverlayView.OnGestureListener {

    private val gestureOverlay by lazy { gesture_overlay }
    private lateinit var gestureLibrary: GestureLibrary
    private var gesture: Gesture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        openOptionsMenu()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        //Check permissions
        val permission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1)
        }

        gestureLibrary = GestureLibraries.fromFile("/sdcard/gestures.txt")
        if(!gestureLibrary.load()) {
            finish()
        }
        gestureOverlay.gestureStrokeType = GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE
        gestureOverlay.addOnGestureListener(this)
        gestureOverlay.addOnGesturePerformedListener { overlay, gesture ->
            val predictions = gestureLibrary.recognize(gesture)
            val topPrediction = predictions.maxBy { it.score }
            topPrediction?.let {
                Toast.makeText(this, topPrediction.name, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_menu, menu)
        return true
    }

    override fun onGestureStarted(overlay: GestureOverlayView?, event: MotionEvent?) {
        Log.d("Gesture", "started")
    }

    override fun onGestureCancelled(overlay: GestureOverlayView?, event: MotionEvent?) {
        Log.d("Gesture", "cancelled")
    }

    override fun onGesture(overlay: GestureOverlayView?, event: MotionEvent?) {
        Log.d("Gesture", "")
        gesture = overlay?.gesture
    }

    override fun onGestureEnded(overlay: GestureOverlayView?, event: MotionEvent?) {
        Log.d("Gesture", "ended")
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.save_gesture){
            try {
                var selectedName: String? = null

                SaveDialogFragment(object : SaveDialogListener {
                    override fun onSaveDialogFinished(fileName: String?) {
                        fileName?.let {

                            gestureLibrary.addGesture(it, gesture)
                            if(!gestureLibrary.save()) {
                                Toast.makeText(applicationContext, "Failed to Save Gesture", Toast.LENGTH_LONG).show()
                            } else {

                                Toast.makeText(
                                    applicationContext,
                                    "Gesture Saved $it",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }).show(supportFragmentManager, "saveDialog")

            } catch (e: Exception) {
                Log.v("Signature Gestures", e.message)
                e.printStackTrace()
            }

        }
        return super.onOptionsItemSelected(item)
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