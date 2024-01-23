package com.example.chatappsoketio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.chatappsoketio.databinding.ActivityLoginBinding
import com.example.chatappsoketio.model.User
import io.agora.CallBack
import io.agora.ConnectionListener
import io.agora.chat.ChatClient
import io.agora.chat.ChatOptions
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import kotlinx.coroutines.runBlocking

class LoginActivity : AppCompatActivity() {


    private fun showLog(text: String) {
        // Show a toast message
        runOnUiThread { Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show() }

        // Write log
        Log.d("AgoraChatQuickStart", text)
    }

    private lateinit var binding: ActivityLoginBinding
    private var isJoined: Boolean = false
    private lateinit var agoraChatClient: ChatClient

    private val appKey: String = "411088405#1269075"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupChatClient()
        setupListeners()


        binding.loginBtn.setOnClickListener {
            val appId = "8acdd6c024334184bc6e3a4d2cc3e755"
            val appCertificate = "1f5fa96c2dff4623815250d5f4b591c7"
            val userId = binding.usernameEdt.text.toString().trim()

            runBlocking {

                val userToken = generateAgoraToken(appId, appCertificate, userId)
                runOnUiThread {
                    agoraChatClient.loginWithAgoraToken(userId, userToken, object : CallBack {
                        override fun onSuccess() {
                            showLog("Signed in")
                            isJoined = true
                            val intent =Intent(this@LoginActivity,MainActivity::class.java)
                            intent.putExtra("userId",userId)
                            startActivity(intent)
                        }
                        override fun onError(code: Int, error: String) {
                            if (code == 200) { // Already joined
                                isJoined = true
                            } else {
                                showLog(error)
                            }
                        }
                    })
                }

            }

        }
    }

    private fun setupChatClient() {
        val options = ChatOptions()
        if (appKey.isEmpty()) {
            showLog("You need to set your AppKey")
            return
        }
        options.appKey = appKey // Set your app key in options
        agoraChatClient = ChatClient.getInstance()
        agoraChatClient.init(this, options) // Initialize the ChatClient
        agoraChatClient.setDebugMode(true) // Enable debug info output
    }

    private fun setupListeners() {

        agoraChatClient.addConnectionListener(object : ConnectionListener {
            override fun onConnected() {
                showLog("Connected")
            }

            override fun onDisconnected(error: Int) {
                if (isJoined) {
                    showLog("Disconnected: $error")
                    isJoined = false
                }
            }
            override fun onLogout(errorCode: Int) {
                showLog("User logging out: $errorCode")
            }

            override fun onTokenExpired() {

            }

            override fun onTokenWillExpire() {

            }
        })
    }

    private suspend fun generateAgoraToken(appId: String, appCertificate: String, userId: String): String {
        val agoraApiEndpoint = "https://api.agora.io/v1/token"

        val requestBody = """
        {
            "key": "$appId",
            "secret": "$appCertificate",
            "channelName": "${System.currentTimeMillis()}",  // Replace with your channel name
            "uid": $userId,
            "expirationTimeInSeconds": 3600  
        }
    """.trimIndent()

        val response = HttpClient().use { client ->
            client.post<String> {
                url(agoraApiEndpoint)
                body = TextContent(requestBody, contentType = ContentType.Application.Json)
            }
        }

        // Parse the response to extract the token
        // Note: This is a simplified example. Handle errors and parsing accordingly.

        return response
    }


}