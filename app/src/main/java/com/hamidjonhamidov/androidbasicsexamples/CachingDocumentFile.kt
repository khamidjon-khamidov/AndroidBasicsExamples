package com.hamidjonhamidov.androidbasicsexamples

import androidx.documentfile.provider.DocumentFile

/**
 * Caching version of a [DocumentFile].
 *
 * A [DocumentFile] will perform a lookup (via the system [ContentResolver]), whenever a
 * property is referenced. This means that a request for [DocumentFile.getName] is a *lot*
 * slower than one would expect.
 *
 * To improve performance in the app, where we want to be able to sort a list of [DocumentFile]s
 * by name, we wrap it like this so the value is only looked up once.
 */
data class CachingDocumentFile(private val documentFile: DocumentFile) {
    val name: String? by lazy { documentFile.name }
    val type: String? by lazy { documentFile.type }

    val isDirectory: Boolean by lazy { documentFile.isDirectory }

    val uri get() = documentFile.uri

    fun rename(newName: String): CachingDocumentFile {
        documentFile.renameTo(newName)
        return CachingDocumentFile(documentFile)
    }

    fun delete(){
        documentFile.delete()
    }
}

fun Array<DocumentFile>.toCachingList(): List<CachingDocumentFile> {
    val list = mutableListOf<CachingDocumentFile>()
    for (document in this) {
        list += CachingDocumentFile(document)
    }
    return list
}