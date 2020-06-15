package com.hamidjonhamidov.androidbasicsexamples

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile

// create file from the given uri
fun Activity.createFileInsideUri(uri: Uri, fileName: String) {
    val docFile = DocumentFile.fromTreeUri(this, uri)
    if (docFile?.canWrite() == true && docFile.isDirectory) {
        docFile.createFile("", fileName)
    }
    Toast.makeText(this, "File created inside folder", Toast.LENGTH_SHORT).show()

}

// delete file from the given uri
fun Activity.deleteFromUri(uri: Uri) {
    val documentFile = DocumentFile.fromSingleUri(this, uri)
    if (documentFile?.canWrite() == true) {
        documentFile.delete()
    }
}


// rename file from the given uri
fun Activity.renameFromUri(uri: Uri, fileName: String) {
    val docFile = DocumentFile.fromTreeUri(this, uri)
    if (docFile?.canWrite() == true) {
        docFile.renameTo(fileName)
        Toast.makeText(this, "File renamed", Toast.LENGTH_SHORT).show()
    }
}