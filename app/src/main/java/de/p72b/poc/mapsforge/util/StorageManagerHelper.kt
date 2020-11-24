package de.p72b.poc.mapsforge.util

import android.os.Build
import android.os.StatFs
import android.os.storage.StorageManager
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

class StorageManagerHelper(private val storageManager: StorageManager) {

    fun hasSpaceOnDisk(filesDir: File, neededSpace: Long): Boolean {
        val availableBytes: Long
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appSpecificInternalDirUuid: UUID = storageManager.getUuidForPath(filesDir)
            availableBytes = storageManager.getAllocatableBytes(appSpecificInternalDirUuid)
            if (availableBytes >= neededSpace) {
                storageManager.allocateBytes(appSpecificInternalDirUuid, neededSpace)
            }
        } else {
            availableBytes = StatFs(filesDir.absolutePath).availableBytes
        }
        return availableBytes >= neededSpace
    }

    fun saveToInternalStorage(outFile: File, bytes: ByteArray): File?{
        return try {
            val fileOutputStream = FileOutputStream(outFile)
            fileOutputStream.write(bytes)
            fileOutputStream.close()

            outFile
        } catch (e: IOException){ // catch the exception
            e.printStackTrace()
            null
        }
    }
}