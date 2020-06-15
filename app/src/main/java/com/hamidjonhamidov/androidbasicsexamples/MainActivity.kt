package com.hamidjonhamidov.androidbasicsexamples

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import com.hamidjonhamidov.androidbasicsexamples.util.SharedPrefs
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

/**
 *  In this example, persistableStoragePermission is requested from the user for particular storage
 *
 *  Directory uri is rececived
 *  Delete/Rename/Create Operations are carried out
 */

class MainActivity : AppCompatActivity() {

    val OPEN_DIRECTORY_REQUEST_CODE = 322
    val OPEN_IMAFGES_REQUEST_CODE = 2342

    private val TAG = "MainActivity"

    val sharedPrefs: SharedPrefs by lazy {
        SharedPrefs(application)
    }

    lateinit var mAdapter: DirectoryEntryAdapter
    lateinit var children: List<CachingDocumentFile>
    lateinit var mainUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindButtons()
        val savedUri = sharedPrefs.getUri()
        savedUri?.let {
            val mDocumentFile = DocumentFile.fromTreeUri(this, Uri.parse(it))
            mDocumentFile?.let {
                mainUri = it.uri
            }
            mDocumentFile?.listFiles()?.toCachingList()?.let {
                children = it
                initRecyclerView(children)
            }
        }
    }

    private fun bindButtons() {
        open_directory.setOnClickListener {

            if (!::mainUri.isInitialized)
                openDirectoryForFullAccess()
            // if directoryUri is already selected, we can select image to delete
            else
                openForImages()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_DIRECTORY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val directoryUri = data?.data ?: return
            Log.d(TAG, "onActivityResult: ${directoryUri.path}")
            mainUri = directoryUri
            sharedPrefs.saveUri(directoryUri.toString())
            showDirectoryContents()
        }

        if (requestCode == OPEN_IMAFGES_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            createAlertDialog {
                val clipData = data?.clipData
                if (clipData != null)
                    for (i in 0 until clipData.itemCount) {
                        val mUri = clipData.getItemAt(i).uri
                        deleteFromUri(mUri)
                    }
            }
        }
    }

    private fun initRecyclerView(children: List<CachingDocumentFile>) {
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            mAdapter = DirectoryEntryAdapter(object : ClickListeners {
                override fun onDocumentClicked(clickedDocument: CachingDocumentFile, view: View) {
                    createPopUp(clickedDocument, view)
                }
            })

            adapter = mAdapter
        }

        mAdapter.setEntries(children)
    }

    private fun showDirectoryContents() {
        val docs = DocumentFile.fromTreeUri(this, mainUri) ?: return
        children = docs.listFiles().toCachingList()
        initRecyclerView(children)
    }

    private fun createPopUp(clickedDocument: CachingDocumentFile, view: View) {
        val popup = PopupMenu(this, view)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.popup_menu, popup.menu)
        popup.setOnMenuItemClickListener { it ->
            when (it.itemId) {
                R.id.rename -> {
                    createDialog("Rename") { fileName ->
                        renameFromUri(clickedDocument.uri, fileName)
                        showDirectoryContents()
                    }
                }

                R.id.delete -> {
                    deleteFromUri(clickedDocument.uri)
                    showDirectoryContents()
                }

                R.id.create_inside -> {
                    createDialog("Create Inside Folder") { fileName ->
                        createFileInsideUri(mainUri, fileName)
                        showDirectoryContents()
                    }
                }
            }
            true
        }
        popup.show()
    }

    private fun createDialog(title: String, okPressed: (str: String) -> Unit) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.rename_layout)
        dialog.findViewById<TextView>(R.id.dialog_title).text = title
        dialog.findViewById<Button>(R.id.btn_ok).setOnClickListener {
            val et = dialog.findViewById<EditText>(R.id.file_name)
            if (et.text.isNotEmpty()) {
                okPressed(et.text.toString())
                dialog.dismiss()
            } else {
                Toast.makeText(this@MainActivity, "Field cannot be empty", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        dialog.show()
    }

    // for better testing it is advisable to choose main external storage directory
    // and create dummy folder inside it
    private fun openDirectoryForFullAccess() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        }
        startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE)
    }

    private fun openForImages() {
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        }
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            OPEN_IMAFGES_REQUEST_CODE
        )
    }

    private fun createAlertDialog(action: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Warning!!!")
            .setMessage("Do you really want to delete selected images")
            .setPositiveButton("Ok") { p0, p1 -> action() }
            .setNegativeButton("Cancel") { p0, p1 -> p0.cancel() }
            .create()
            .show()
    }
}
















