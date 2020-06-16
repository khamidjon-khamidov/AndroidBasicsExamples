package com.hamidjonhamidov.androidbasicsexamples

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.documentfile.provider.DocumentFile
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val OPEN_DIRECTORY_REQUEST_CODE = 322
    val OPEN_IMAFGES_REQUEST_CODE = 2342
    val OPEN_FILES_REQUEST_CODE = 22431
    val ENCRYPTED_FILES = "Encrypted Files"
    val DECRYPTED_FILES = "Decrypted Files"

    private val TAG = "MainActivity"

    var mUri: Uri? = null

    val sharedPrefs: SharedPrefs by lazy {
        SharedPrefs(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mUri = Uri.parse(sharedPrefs.getUri())
        mUri?.let {
            setMessage(mUri!!.path.toString())
        }
        bindViews()
    }

    private fun bindViews(){
        btn_location.setOnClickListener {
            if(mUri!=null){
                showAlertDialog("Previous location exists! Do you want to choose another location?"){
                    openDirectory()
                }
            } else {
                openDirectory()
            }
        }

        btn_encrypt.setOnClickListener {
            if(getEditTextMessage().isEmpty()){
                showAlertDialog("Encryptoin Code must not be empty!"){}
            }
            else if (mUri==null){
                showAlertDialog("You should choose destination location first!") {}
            }
            else {
                openForImage()
            }
        }

        btn_decrypt.setOnClickListener {
            if(getEditTextMessage().isEmpty()){
                showAlertDialog("Decryption Code must not be empty!"){}
            } else {
                openForFile()
            }
        }
    }

    private fun showAlertDialog(message: String, action: ()->Unit){
        AlertDialog.Builder(this)
            .setTitle("Warning")
            .setMessage(message)
            .setPositiveButton("Ok") {p0, p1 -> action()}
            .setNegativeButton("Cancel") {p0, p1 -> p0.dismiss()}
            .create()
            .show()
    }

    private fun openDirectory() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        }
        startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE)
    }

    private fun openForImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        }
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

        startActivityForResult(
            Intent.createChooser(intent, "Select Picture To Encrypt"),
            OPEN_IMAFGES_REQUEST_CODE
        )
    }

    private fun openForFile(){
        val intent = Intent()
        intent.type = "*/*"
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        }
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

        startActivityForResult(
            Intent.createChooser(intent, "Select File To Decrypt"),
            OPEN_IMAFGES_REQUEST_CODE
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_DIRECTORY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val directoryUri = data?.data ?: return
            mUri = directoryUri
            setMessage(mUri!!.path.toString())
            sharedPrefs.saveUri(directoryUri.toString())
        }

        if (requestCode == OPEN_IMAFGES_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data ?: return
            val imageFile = DocumentFile.fromSingleUri(this, imageUri) ?: return

            val destFolder = makeChild(mUri!!, ENCRYPTED_FILES, true) ?: return
            val sourceFile = makeChild(destFolder.uri, imageFile.name ?: return, false) ?: return


            if(encrypt(imageUri, sourceFile.uri, getEditTextMessage(), "KHAMIDJON")) {
                Toast.makeText(this, "File Encrypted", Toast.LENGTH_SHORT).show()
            }
        }

        if (requestCode == OPEN_FILES_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data ?: return
            val imageFile = DocumentFile.fromSingleUri(this, imageUri) ?: return

            val destFolder = makeChild(mUri!!, DECRYPTED_FILES, true) ?: return
            val sourceFile = makeChild(destFolder.uri, imageFile.name ?: return, false) ?: return

            if(decrypt(imageUri, sourceFile.uri, getEditTextMessage(), "KHAMIDJON")) {
                Toast.makeText(this, "File Decrypted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getEditTextMessage() = et_password.text.toString()

    private fun setMessage(msg: String){
        tv_message.text = msg
    }
}


