package com.jacobchapman.contactgestures

import android.content.pm.PackageManager
import android.gesture.Gesture
import android.gesture.GestureLibraries
import android.gesture.GestureLibrary
import android.gesture.GestureOverlayView
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

class SaveGestureFragment : Fragment(), GestureOverlayView.OnGestureListener {

    private lateinit var gestureOverlay: GestureOverlayView
    private lateinit var gestureLibrary: GestureLibrary
    private var gesture: Gesture? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_draw_gesture, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Check permissions
        val permission = ActivityCompat.checkSelfPermission(activity!!, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity!!,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1)
        }

        gestureLibrary = GestureLibraries.fromFile("/sdcard/gestures.txt")
        if(!gestureLibrary.load()) {
            activity?.finish()
        }
        gestureOverlay = view.findViewById(R.id.gesture_overlay)
        gestureOverlay.gestureStrokeType = GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE
        gestureOverlay.addOnGestureListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.save_menu, menu)
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
                                Toast.makeText(this@SaveGestureFragment.context, "Failed to Save Gesture", Toast.LENGTH_LONG).show()
                            } else {

                                Toast.makeText(
                                    this@SaveGestureFragment.context,
                                    "Gesture Saved $it",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }).show(fragmentManager, "saveDialog")

            } catch (e: Exception) {
                Log.v("Signature Gestures", e.message)
                e.printStackTrace()
            }

        }
        return super.onOptionsItemSelected(item)
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
}