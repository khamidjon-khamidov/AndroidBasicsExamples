package com.hamidjonhamidov.androidbasicsexamples

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import kotlinx.android.synthetic.main.activity_main.*
import net.sqlcipher.database.SQLiteDatabase

class MainActivity : AppCompatActivity() {

    val DB_PATH: String by lazy {
        "$filesDir/demo.db"
    }
    val DB_TABLE_NAME = "TABLE_1"
    val DB_COLUMN1_NAME = "name"
    val DB_COLUMN2_SURNAME = "surname"


    val OPEN_DIRECTORY_REQUEST_CODE = 322
    private val TAG = "MainActivity"

    private val sharedPrefs: SharedPrefs by lazy {
        SharedPrefs(application)
    }

    private val database: SQLiteDatabase by lazy {
        SQLiteDatabase.openOrCreateDatabase(
            DB_PATH,
            sharedPrefs.getPassword(),
            null
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize sqlite libraries
        SQLiteDatabase.loadLibs(this)

        bindViews()
    }

    private fun bindViews() {
        setPasswordTv(sharedPrefs.getPassword())

        btn_insert.setOnClickListener {
            if (getName().isNotEmpty() && getSurname().isNotEmpty()) {
                saveToDb(getName(), getSurname())
                showToast("Message saved to DB")
            } else {
                showToast("Sorry, all fields must be filled")
            }
        }

        btn_change_password.setOnClickListener {
            if (getPassword().isNotEmpty()) {
                database.changePassword(getPassword())
                sharedPrefs.savePassword(getPassword())
                setPasswordTv(getPassword())
                showToast("Password changed to ${getPassword()}")
            } else {
                showToast("Password field cannot be empty")
            }
        }

        btn_load_password.setOnClickListener {
            et_password_db.setText(sharedPrefs.getPassword())
            showToast("Password loaded")
        }

        btn_delete_all.setOnClickListener {
            database.delete(DB_TABLE_NAME, null, null)
            showToast("Deleted Successfully")
        }

        btn_load_data.setOnClickListener {
            loadFromDb().let {
                if (it.isEmpty()) {
                        setMessage("Database is empty")
                } else {
                    setMessage(it)
                }
            }
        }

        btn_load_data.callOnClick()
    }

    private fun saveToDb(name: String, surname: String) {
        createTableIfNotExist()
        database.execSQL(
            "insert into $DB_TABLE_NAME($DB_COLUMN1_NAME,  $DB_COLUMN2_SURNAME) values(?, ?)",
            arrayOf(
                name, surname
            )
        )
    }

    private fun loadFromDb(): String {
        var s = ""
        var i = 0

        createTableIfNotExist()
        val cursor = database.rawQuery("select * from $DB_TABLE_NAME", null)
        cursor?.moveToFirst()
        if (!cursor.isAfterLast) {
            do {
                s += "col_${i++}: name:${cursor.getString(0)} surname:${cursor.getString(1)}\n"
            } while (cursor.moveToNext())

            cursor.close()
            return s
        }

        return s
    }

    private fun createTableIfNotExist() {
        database.execSQL("create table if not exists $DB_TABLE_NAME($DB_COLUMN1_NAME,  $DB_COLUMN2_SURNAME)")
    }

    private fun openDirectory() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        }
        startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE)
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun getName() = et_name.text.toString()

    private fun getSurname() = et_surname.text.toString()

    private fun getPassword() = et_password_db.text.toString()

    private fun setMessage(msg: String) {
        tv_message.text = msg
    }

    @SuppressLint("SetTextI18n")
    private fun setPasswordTv(password: String) {
        tv_password.setText("password: $password")
    }

    override fun onDestroy() {
        super.onDestroy()
        database.close()
    }
}











