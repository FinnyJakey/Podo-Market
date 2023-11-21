import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.podomarket.R
import com.example.podomarket.chat.ExampleChat
import com.example.podomarket.model.ChatModel

class ChatRoomBubbleRecyclerViewAdapter(private val dataList: List<ChatModel>, private val myUuid: String) : RecyclerView.Adapter<ChatRoomBubbleRecyclerViewAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.message)
        val chatTimeTextView: TextView = itemView.findViewById(R.id.chat_time)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutResId = if (viewType == 0) {
            R.layout.chat_room_me_item
        } else {
            R.layout.chat_room_you_item
        }
        val itemView = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        return MyViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = dataList[position]
        holder.messageTextView.text = item.message
        holder.chatTimeTextView.text = item.createdAt.toDate().time.toString()
    }

    override fun getItemViewType(position: Int): Int {
        val item = dataList[position]
        return if (item.userId == myUuid) {
            0
        } else {
            1
        }
    }
    override fun getItemCount(): Int {
        return dataList.size
    }
}
