package com.example.chatappsoketio.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappsoketio.R
import com.example.chatappsoketio.model.ChatMessage
import com.example.chatappsoketio.model.Message

class ChatAdapter(private val messages: MutableList<Message> = mutableListOf()) :
    RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val sentMsg: TextView = itemView.findViewById(R.id.sent_msg_tv)
        val getMsg: TextView = itemView.findViewById(R.id.recive_msg_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
//        holder.messageTextView.text = message.content

        // Customize appearance based on whether it's a sent message or received message
        if (message.isSentMessage) {
            holder.sentMsg.visibility=View.VISIBLE
            holder.getMsg.visibility=View.GONE
            holder.sentMsg.text=message.content
        } else {
            holder.sentMsg.visibility=View.GONE
            holder.getMsg.visibility=View.VISIBLE
            holder.getMsg.text=message.content
        }
    }

    override fun getItemCount(): Int = messages.size
    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }
}
