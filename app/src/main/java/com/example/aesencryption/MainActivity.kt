package com.example.aesencryption

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.view.View
import android.widget.Button
import android.widget.EditText
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class MainActivity : AppCompatActivity() {

    lateinit var Input : EditText
    lateinit var Output : EditText
    lateinit var Encryptd : Button
    lateinit var Decypted : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       val keyGenerator : KeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "Android Key Provider")
       val keyGenParamenter = KeyGenParameterSpec.Builder("AESKeys", KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
               .setBlockModes(KeyProperties.BLOCK_MODE_CBC).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build()

        keyGenerator.init(keyGenParamenter)
        keyGenerator.generateKey()


        Input =  findViewById<View>(R.id.input) as EditText
        Output = findViewById<View>(R.id.output) as EditText
        Encryptd = findViewById<View>(R.id.encryptd) as Button
        Decypted = findViewById<View>(R.id.decypted) as Button

        Encryptd.setOnClickListener (object : View.OnClickListener {

            override fun onClick (view : View?)
            {
                val InputString =  Input.text.toString()
                val encryptedInput = EncryptedData(InputString)
                val encryptedData = encryptedInput.second.toString(Charsets.UTF_8)
                val OutputText = Output.setText(encryptedData)

            }
        })

        Decypted.setOnClickListener (object : View.OnClickListener {
           override fun onClick (view : View?)
            {
                val InputString = Input.text.toString()
                val decryptedInput = EncryptedData(InputString)
                val decrypteddata = DecryptedData(decryptedInput.first, decryptedInput.second)
                val OutputText = Output.setText(decrypteddata)
            }
        })




    }

    fun getKey () : SecretKey
    {
        val keySotore : KeyStore = KeyStore.getInstance("Android KeyStore AES")
        keySotore.load(null)

        val keyEntry = keySotore.getEntry("AESKeys", null) as KeyStore.SecretKeyEntry

       return  keyEntry.secretKey

    }

    fun EncryptedData (dataString : String) : Pair<ByteArray, ByteArray>
    {
        val cipher : Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

        var temp = dataString
        while (temp.toByteArray().size % 16 != 0)
            temp += "\u0020"

        cipher.init(Cipher.ENCRYPT_MODE, getKey())
        val ivByte = cipher.iv
        val dofinal = cipher.doFinal(byteArrayOf())

        val encryptionResult = Pair(ivByte, dofinal)

        return encryptionResult
    }




    fun DecryptedData (ivByteArray : ByteArray, dataByteArray: ByteArray) : String
    {
      val cipher : Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
       val AESIvSpec : IvParameterSpec = IvParameterSpec(ivByteArray)
       cipher.init(Cipher.DECRYPT_MODE, getKey(), AESIvSpec)

        val decryptionResult = cipher.doFinal(dataByteArray).toString(Charsets.UTF_8).trim()

       return decryptionResult
    }
}