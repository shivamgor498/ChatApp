package com.example.shivam.placement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_new_message_activity.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {
    val adapter = GroupAdapter<ViewHolder>()
    var user:User ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        user = intent.getParcelableExtra<User>(new_message_activity.USER_KEY)
        supportActionBar?.title = user?.username
        listenForMessages()
        chat_send.setOnClickListener {
            performSendMessage()
            //adapter.add(ChattoItem("123",user!!))
        }
        //listenForMessages()
        chat_recyclerview.adapter = adapter
        chat_recyclerview.layoutManager = LinearLayoutManager(applicationContext)
    }
    private fun listenForMessages(){
        val fromID = FirebaseAuth.getInstance().uid
        val toID = user?.uid

        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromID/$toID")
        //val ref1 = FirebaseDatabase.getInstance().getReference("/user-messages/$toID/$fromID")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
               // Toast.makeText(applicationContext,"${chatMessage?.text} + ${chatMessage?.fromID} + ${chatMessage?.toID}",Toast.LENGTH_SHORT).show()
                if(chatMessage!=null)
                {
                    if(chatMessage.fromID==FirebaseAuth.getInstance().uid && chatMessage.toID==user?.uid)
                        adapter.add(ChatfromItem(chatMessage.text,Dashboard.currentUser!!))
                    else if(chatMessage.fromID==user?.uid && chatMessage.toID==FirebaseAuth.getInstance().uid)
                        adapter.add(ChattoItem(chatMessage.text,user!!))
                }
            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }
            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }
    private fun performSendMessage() {
        val text = chat_log_text.text.toString()
        val fromID = FirebaseAuth.getInstance().uid
        val toID = user?.uid
       // val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromID/$toID").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toID/$fromID").push()
        if(reference.key==null || fromID==null || toReference.key==null)
            return
        val chatMessage = ChatMessage(reference.key!!,text,fromID!!,toID!!,System.currentTimeMillis())
        reference.setValue(chatMessage)
                .addOnSuccessListener {
                    chat_log_text.text.clear()
                    chat_recyclerview.scrollToPosition(adapter.itemCount -1)
                }
        toReference.setValue(chatMessage)
    }
}
class ChatMessage(val id: String,val text: String,val fromID:String, val toID:String, val Timestamp : Long)
{
    constructor(): this("","","","",-1)
}
class ChatfromItem(val text: String,val user:User) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.text_from_row.text = text
        val uri = user.profileUrl
        Picasso.get().load(uri).into(viewHolder.itemView.image_chat_from_row)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}
class ChattoItem(val text: String,val user:User) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.text_to_row.text = text
        val uri = user.profileUrl
        Picasso.get().load(uri).into(viewHolder.itemView.image_chat_to_row)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}
