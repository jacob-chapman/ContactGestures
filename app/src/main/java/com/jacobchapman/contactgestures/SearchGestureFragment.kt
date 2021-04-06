package com.jacobchapman.contactgestures

import android.content.Intent
import android.content.pm.PackageManager
import android.gesture.Gesture
import android.gesture.GestureLibraries
import android.gesture.GestureLibrary
import android.gesture.GestureOverlayView
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.recoginze.fragment_search_gesture.*

class SearchGestureFragment  : Fragment(), OnContactClickedListener {
    private lateinit var gestureOverlay: GestureOverlayView

    private lateinit var gestureLibrary: GestureLibrary
    private var gesture: Gesture? = null
    private lateinit var contactsList : RecyclerView
    private lateinit var clearSearchFab: FloatingActionButton
    private val contactsAdapter: ContactsAdapter by lazy { ContactsAdapter(mutableListOf(), this) }
    private var searchString: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search_gesture, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Check permissions
        val storagePermission = ActivityCompat.checkSelfPermission(activity!!, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if(storagePermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity!!,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1)
        }

        val contactPermission = ActivityCompat.checkSelfPermission(activity!!, android.Manifest.permission.READ_CONTACTS)
        val writeContactsPermission = ActivityCompat.checkSelfPermission(activity!!, android.Manifest.permission.WRITE_CONTACTS)
        if(contactPermission != PackageManager.PERMISSION_GRANTED && writeContactsPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity!!,
                arrayOf(android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.WRITE_CONTACTS),
                1)
        }

        val callPermission = ActivityCompat.checkSelfPermission(activity!!, android.Manifest.permission.CALL_PHONE)
        if(callPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity!!,
                arrayOf(android.Manifest.permission.CALL_PHONE), 1)
        }

        gestureLibrary = GestureLibraries.fromRawResource(context!!, R.raw.gestures)
        if(!gestureLibrary.load()) {
            activity?.finish()
        }
        gestureOverlay = view.findViewById(R.id.gesture_overlay)
        gestureOverlay.gestureStrokeType = GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE
        gestureOverlay.addOnGesturePerformedListener { _, gesture ->
                        val predictions = gestureLibrary.recognize(gesture)
            val topPrediction = predictions.maxBy { it.score }
            topPrediction?.let {
                Toast.makeText(context, topPrediction.name, Toast.LENGTH_LONG).show()
            }

            activity?.let {
                searchString = "$searchString${topPrediction?.name}"
                Log.d("Search String", searchString)
                var contactsList: Array<String> = arrayOf("%$searchString%")
                val cursor = it.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME),
                    "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} Like ?", contactsList, null)
                if(cursor.count > 0){
                    contactsAdapter.contacts = mutableListOf()
                }
                while (cursor.moveToNext())
                {
                    val name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    contactsAdapter.contacts.add(ContactModel(name,""))
                    Log.d("Contacts", name)
                }
                contactsAdapter.notifyDataSetChanged()
            }

        }

        contactsList = view.findViewById(R.id.contacts_list)
        contactsList.adapter = contactsAdapter
        contactsList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        //Clear Search Button
        clearSearchFab = view.findViewById(R.id.clear_search_fab)
        clearSearchFab.setOnClickListener {
            searchString = ""
            contactsAdapter.contacts.clear()
            contactsAdapter.notifyDataSetChanged()
        }
    }

    override fun contactClicked(contact: ContactModel) {
        var intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:6147169657")
        startActivity(intent)
    }


}
