package com.alfie.whitepaper.core.utils

import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Arrays
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec


fun generateAesSecretKey(
    SALT2: String = "strong_salt_value",
    username: String = "user_name",
    password: String = "strong_password"
): ByteArray? {
    var key: ByteArray? = (SALT2 + username + password).toByteArray()
    var secretKeySpec: SecretKey? = null
    try {
        val sha = MessageDigest.getInstance("SHA-1")
        key = sha.digest(key)
        key = Arrays.copyOf(key, 16)
        secretKeySpec = SecretKeySpec(key, "AES")
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return secretKeySpec!!.encoded
}


fun encodeFile(secretKey: ByteArray?, fileData: ByteArray?): ByteArray? {
    val skeySpec = SecretKeySpec(secretKey, "AES")
    var encrypted: ByteArray? = null
    try {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec)
        encrypted = cipher.doFinal(fileData)

        // Now write your logic to save encrypted data to sdcard here
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    } catch (e: NoSuchPaddingException) {
        e.printStackTrace()
    } catch (e: InvalidKeyException) {
        e.printStackTrace()
    } catch (e: IllegalBlockSizeException) {
        e.printStackTrace()
    } catch (e: BadPaddingException) {
        e.printStackTrace()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return encrypted
}

fun decodeFile(key: ByteArray?, fileData: ByteArray?): ByteArray? {
    val skeySpec = SecretKeySpec(key, "AES")
    var decrypted: ByteArray? = null
    try {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, skeySpec)
        decrypted = cipher.doFinal(fileData)
    } catch (e: NoSuchAlgorithmException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    } catch (e: NoSuchPaddingException) {
        e.printStackTrace()
    } catch (e: InvalidKeyException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    } catch (e: IllegalBlockSizeException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    } catch (e: BadPaddingException) {
        e.printStackTrace()
    } catch (e: java.lang.Exception) {
        // for all other exception
        e.printStackTrace()
    }
    return decrypted
}