import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.podomarket.R
import com.example.podomarket.chat.ExampleChat

class ChatRoomRecyclerViewAdapter(private val dataList: List<ExampleChat>, private val itemClickListener: OnItemClickListener?) : RecyclerView.Adapter<ChatRoomRecyclerViewAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.message)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener?.onItemClick(position)
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val item = dataList[viewType]
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
        holder.textView.text = item.message
    }

    override fun getItemViewType(position: Int): Int {
        val item = dataList[position]
        return if (item.sender == "me") {
            0
        } else {
            1
        }
    }
    override fun getItemCount(): Int {
        return dataList.size
    }
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}
