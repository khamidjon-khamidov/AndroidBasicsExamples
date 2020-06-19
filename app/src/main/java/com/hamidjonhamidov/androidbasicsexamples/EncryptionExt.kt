package com.hamidjonhamidov.androidbasicsexamples

import android.R.attr
import android.app.Activity
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.spec.SecretKeySpec


fun Activity.encrypt(curLocUri: Uri, newLocUri: Uri, password: String, salt: String) : Boolean{
    // opening file input/outputStreams
    val fis = contentResolver.openInputStream(curLocUri) ?: return false
    val fos = contentResolver.openOutputStream(newLocUri) ?: return false

    try {
        var key: ByteArray = ("$salt$password").toByteArray(Charsets.UTF_8)
        val sha: MessageDigest = MessageDigest.getInstance("SHA-1")
        key = sha.digest(key)
        key = key.copyOf(16)
        val sks = SecretKeySpec(key, "AES")
        val cipher: Cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, sks)
        val cos = CipherOutputStream(fos, cipher)
        var b: Int
        val d = ByteArray(8)
        while (fis.read(d).also { b = it } != -1) {
            cos.write(d, 0, b)
        }
        cos.flush()
        cos.close()
        return true
    } catch (e: Throwable){
        fis.close()
        fos.close()
        return false
    }
}

fun Activity.decrypt(curLocUri: Uri, newLocUri: Uri, password: String, salt: String): Boolean {
    // opening file input/outputStreams
    val fis = contentResolver.openInputStream(curLocUri) ?: return false
    val fos = contentResolver.openOutputStream(newLocUri) ?: return false

    try {
        var key: ByteArray = ("$salt$password").toByteArray(Charsets.UTF_8)
        val sha = MessageDigest.getInstance("SHA-1")
        key = sha.digest(key)
        key = key.copyOf(16)
        val sks = SecretKeySpec(key, "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, sks)
        val cis = CipherInputStream(fis, cipher)
        var b: Int
        val d = ByteArray(8)
        while (cis.read(d).also { b = it } != -1) {
            fos.write(d, 0, b)
        }
        fos.flush()
        fos.close()
        cis.close()
        return true
    } catch (e: Throwable){
        fos.flush()
        fos.close()
        return false
    }
}

fun Activity.makeChild(uri: Uri, childName: String, isDirectory: Boolean): DocumentFile? {
    val docFile = DocumentFile.fromTreeUri(this, uri) ?: return null
    // if file with the given uri is not directory, file cannot be created inside it
    // so return null
    if (!docFile.isDirectory) return null

    // if the file already exists return the file/directory
    for(file in docFile.listFiles()){
        if(file.name==childName)
            return file
    }

    // if the child file is directory, return the directory
    if (isDirectory)
        return docFile.createDirectory(childName)

    // if the file is child return newly created child file
    return docFile.createFile("", childName)
}