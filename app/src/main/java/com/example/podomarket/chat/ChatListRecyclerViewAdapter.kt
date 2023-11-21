import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.podomarket.R
import com.example.podomarket.model.ChatRoomModel
import com.example.podomarket.viewmodel.AuthViewModel
import com.google.firebase.Timestamp

class ChatListRecyclerViewAdapter(private val dataList: List<ChatRoomModel>, private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<ChatListRecyclerViewAdapter.MyViewHolder>() {
    private var authviewModel = AuthViewModel()
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameText: TextView = itemView.findViewById(R.id.chat_list_username)
        val messageTimeText: TextView = itemView.findViewById(R.id.chat_list_message_time)
        val messageText: TextView = itemView.findViewById(R.id.chat_list_message_text)
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(position)
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        return MyViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val chatRoom : ChatRoomModel = dataList[position]
        var currentUserId = authviewModel.getCurrentUserUid()
        var anotherUserId = chatRoom.participants[0]
        if(currentUserId == chatRoom.participants[0]) anotherUserId = chatRoom.participants[1]

        authviewModel.getUserName(anotherUserId) { name ->
            if (name != null) {
                //대입
                holder.userNameText.text = name
                holder.messageTimeText.text = calculateTimeAgo(chatRoom.recentTime)
                if(chatRoom.chats.isEmpty())  holder.messageText.text = " "
                else holder.messageText.text = chatRoom.chats[chatRoom.chats.size - 1].message
            } else {
                println("가져오기 실패")
            }
        }
    }
    override fun getItemCount(): Int {
        return dataList.size
    }
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    private fun calculateTimeAgo(createdAt: Timestamp): String {
        val currentTime = Timestamp.now()
        val timeDifferenceMillis = currentTime.toDate().time - createdAt.toDate().time

        val minutes = (timeDifferenceMillis / (1000 * 60)).toInt()

        return when {
            minutes < 60 -> "${minutes}분 전"
            minutes < 24 * 60 -> "${minutes / 60}시간 전"
            else -> "${minutes / (24 * 60)}일 전"
        }
    }
}
