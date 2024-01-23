package com.example.chatappsoketio

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatappsoketio.adapter.ChatAdapter
import com.example.chatappsoketio.adapter.ChatItemDecoration
import com.example.chatappsoketio.databinding.ActivityMainBinding
import com.example.chatappsoketio.db.MessageDataSource
import com.example.chatappsoketio.model.Message
import io.agora.CallBack
import io.agora.ConnectionListener
import io.agora.chat.ChatClient
import io.agora.chat.ChatMessage
import io.agora.chat.ChatOptions
import io.agora.chat.TextMessageBody



class MainActivity : AppCompatActivity() {

    private fun showLog(text: String) {
        // Show a toast message
        runOnUiThread { Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show() }

        // Write log
        Log.d("AgoraChatQuickStart", text)
    }

    private val userId: String = "ak24"
    private val token: String = "007eJxTYPDk+v7jZgqDvN3ynz6LP79d07NtctqiM9b39Lf9izT9Xb9dgcEiMTklxSzZwMjE2NjE0MIkKdks1TjRJMUoOdk41dzU9GDd+tSGQEaGpLxyRkYGVgZGIATxVRhSkxItjRPNDHSTLJOTdA0NU1N1k4zNjXUTk8xSjYxNzZIsktIAxWQrGA=="
    private val appKey: String = "411088405#1269075"

    private lateinit var agoraChatClient: ChatClient
    private var isJoined: Boolean = false
    lateinit var binding:ActivityMainBinding
    private lateinit var chatAdapter:ChatAdapter
    val messageDataSource = MessageDataSource(this)

    lateinit var msgList: MutableList<Message>

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = com.example.chatappsoketio.databinding.ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        messageDataSource.open()
        msgList= messageDataSource.getAllMessages().toMutableList()
        chatAdapter = ChatAdapter(msgList)

        setupChatClient() // Initialize the ChatClient
        setupListeners()// Add event listeners
        binding.msgView.layoutManager=LinearLayoutManager(this)
        binding.msgView.setHasFixedSize(true)
        binding.msgView.addItemDecoration(ChatItemDecoration(this))
        binding.msgView.adapter=chatAdapter

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
        // Add message event callbacks
        agoraChatClient.chatManager().addMessageListener { messages ->
            for (message in messages) {
                runOnUiThread {
                    displayMessage(
                        (message.body as TextMessageBody).message,
                        false
                    )
                }
                showLog(
                    "Received a " + message.type.name
                            + " message from " + message.from
                )
            }
        }

        // Add connection event callbacks
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
                // The token has expired
            }

            override fun onTokenWillExpire() {
                // The token is about to expire. Get a new token
                // from the token server and renew the token.
            }
        })
    }
    fun joinLeave(view: View?) {
        if (isJoined) {
            agoraChatClient.logout(true, object : CallBack {
                override fun onSuccess() {
                    showLog("Sign out success!")
                    runOnUiThread { binding.btnJoinLeave.text = "Join" }
                    isJoined = false
                }
                override fun onError(code: Int, error: String) {
                    showLog(error)
                }
            })
        } else {
            agoraChatClient.loginWithAgoraToken(userId, token, object : CallBack {
                override fun onSuccess() {
                    showLog("Signed in")
                    isJoined = true
                    runOnUiThread { binding.btnJoinLeave.text = "Leave" }
                }

                override fun onError(code: Int, error: String) {
                    if (code == 200) { // Already joined
                        isJoined = true
                        runOnUiThread { binding.btnJoinLeave.text = "Leave" }
                    } else {
                        showLog(error)
                    }
                }
            })
        }
    }
    fun sendMessage(view: View?) {
        val toSendName =
            binding.etRecipient.text.toString().trim { it <= ' ' }
        val content: String = binding.etMessageText.text.toString().trim()
        if (toSendName.isEmpty() || content.isEmpty()) {
            showLog("Enter a recipient name and a message")
            return
        }
        // Create a ChatMessage
        val message = ChatMessage.createTextSendMessage(content, toSendName)

        // Set the message callback before sending the message
        message.setMessageStatusCallback(object : CallBack {
            override fun onSuccess() {
                showLog("Message sent")
                runOnUiThread {
                    displayMessage(content, true)
                    messageDataSource.insertMessage(Message(content,true,System.currentTimeMillis()))
                    // Clear the box and hide the keyboard after sending the message
                    binding.etMessageText.setText("")
                    val inputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(
                        binding.etMessageText.applicationWindowToken,
                        0
                    )
                }
            }

            override fun onError(code: Int, error: String) {
                showLog(error)
            }
        })

        // Send the message
        agoraChatClient.chatManager().sendMessage(message)
    }

//    fun displayMessage(messageText: String?, isSentMessage: Boolean) {
//        // Create a new TextView
//        val messageTextView = TextView(this)
//        messageTextView.text = messageText
//        messageTextView.setPadding(10, 10, 10, 10)
//
//        val params = LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT
//        )
//        if (isSentMessage) {
//            params.gravity = Gravity.END
//            messageTextView.setBackgroundColor(Color.parseColor("#DCF8C6"))
//            params.setMargins(100, 25, 15, 5)
//        } else {
//            messageTextView.setBackgroundColor(Color.parseColor("white"))
//            params.setMargins(15, 25, 100, 5)
//        }
//        // Add the message TextView to the LinearLayout
//        binding.messageList.addView(messageTextView, params)
//    }

    fun displayMessage(messageText: String?, isSentMessage: Boolean) {
//        val message = com.example.chatappsoketio.model.ChatMessage(messageText ?: "", isSentMessage)
        val message= Message(messageText.toString(),isSentMessage,System.currentTimeMillis())
        chatAdapter.addMessage(message)
    }
}