package com.example.examenesseq.util

import android.util.Base64
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

object EncryptionHelper {

    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/ECB/PKCS5Padding"
    private const val SECRET_KEY = "tu_clave_secreta"

    fun encrypt(value: String): String {
        val key = generateKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedValue = cipher.doFinal(value.toByteArray())
        return Base64.encodeToString(encryptedValue, Base64.DEFAULT)
    }

    fun decrypt(value: String): String {
        val key = generateKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key)
        val decryptedValue = cipher.doFinal(Base64.decode(value, Base64.DEFAULT))
        return String(decryptedValue)
    }

    private fun generateKey(): Key {
        val keyGenerator = KeyGenerator.getInstance(ALGORITHM)
        val secretKey = SECRET_KEY.toByteArray()
        return SecretKeySpec(secretKey, ALGORITHM)
    }
}
