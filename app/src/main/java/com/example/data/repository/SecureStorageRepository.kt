package com.example.data.repository

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import java.io.File

class SecureStorageRepository(private val context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    fun saveEncryptedFile(filename: String, content: String) {
        val file = File(context.filesDir, filename)
        if (file.exists()) {
            file.delete()
        }

        val encryptedFile = EncryptedFile.Builder(
            context,
            file,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        encryptedFile.openFileOutput().use { outputStream ->
            outputStream.write(content.toByteArray(Charsets.UTF_8))
        }
    }

    fun readEncryptedFile(filename: String): String? {
        val file = File(context.filesDir, filename)
        if (!file.exists()) {
            return null
        }

        val encryptedFile = EncryptedFile.Builder(
            context,
            file,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        return try {
            encryptedFile.openFileInput().use { inputStream ->
                val bytes = inputStream.readBytes()
                String(bytes, Charsets.UTF_8)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun deleteEncryptedFile(filename: String) {
        val file = File(context.filesDir, filename)
        if (file.exists()) {
            file.delete()
        }
    }
}
