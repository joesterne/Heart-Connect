package com.example.data.repository

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SecureStorageRepository(private val context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    suspend fun saveEncryptedFile(filename: String, content: String) = withContext(Dispatchers.IO) {
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

    suspend fun readEncryptedFile(filename: String): String? = withContext(Dispatchers.IO) {
        val file = File(context.filesDir, filename)
        if (!file.exists()) {
            return@withContext null
        }

        val encryptedFile = EncryptedFile.Builder(
            context,
            file,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        return@withContext try {
            encryptedFile.openFileInput().use { inputStream ->
                val bytes = inputStream.readBytes()
                String(bytes, Charsets.UTF_8)
            }
        } catch (e: Exception) {
            android.util.Log.e("SecureStorage", "Secure storage operation failed", e)
            null
        }
    }
    
    suspend fun deleteEncryptedFile(filename: String) = withContext(Dispatchers.IO) {
        val file = File(context.filesDir, filename)
        if (file.exists()) {
            file.delete()
        }
    }
}
