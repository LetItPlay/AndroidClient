package com.letitplay.maugry.letitplay.utils.ext

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.support.v4.content.FileProvider
import java.io.File

private const val FILE_AUTHORITY = "com.letitplay.maugry.letitplay.fileprovider"

fun Context.getUriForFile(file: File): Uri {
    return FileProvider.getUriForFile(this, FILE_AUTHORITY, file)
}

fun Context.imageFile(filename: String): File {
    val storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File("$storageDir/$filename")
}

fun Context.getUriForImageFile(filename: String): Uri {
    return getUriForFile(imageFile(filename))
}