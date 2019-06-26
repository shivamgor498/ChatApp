package com.example.shivam.placement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message_activity.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class new_message_activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message_activity)
        supportActionBar?.title = "Select User"
       /* val adapter = GroupAdapter<ViewHolder>()
        recycler_viewmessage.adapter = adapter
        adapter.add(UserItem())
        adapter.add(UserItem())
        adapter.add(UserItem())
        recycler_viewmessage.layoutManager =  LinearLayoutManager(this)*/
        fetchUsers()
    }
    companion object {
        val USER_KEY = "USER_KEY"
    }
    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                recycler_viewmessage.adapter = adapter
                p0.children.forEach {
                    val user = it.getValue(User::class.java)
         //           Toast.makeText(applicationContext,user?.username,Toast.LENGTH_SHORT).show()
                    if (user != null && user.username!=Dashboard.currentUser?.username) {
                        adapter.add(UserItem(user))
                    }
                }
                adapter.setOnItemClickListener{item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context,ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY,userItem.user)
                    startActivity(intent)
                    finish()
                }
                recycler_viewmessage.layoutManager = LinearLayoutManager(applicationContext)
            }
        })
    }
}
class UserItem(val user:User): Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username.text = user.username
        Picasso.get().load(user.profileUrl).into(viewHolder.itemView.user_image)
    }
}
