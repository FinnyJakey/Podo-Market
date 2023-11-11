import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.podomarket.R
import com.example.podomarket.model.BoardModel

class ProductRecyclerViewAdapter(private var dataList: List<BoardModel>, private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<ProductRecyclerViewAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.product_title)
        val userNameTextView: TextView = itemView.findViewById(R.id.product_user_name)
        val priceTextView: TextView = itemView.findViewById(R.id.product_price)

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
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = dataList[position]

        // 데이터를 ViewHolder에 바인딩
        holder.titleTextView.text = item.title
        holder.userNameTextView.text = item.userName
        holder.priceTextView.text = "${item.price} 원"
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setBoards(newBoards: List<BoardModel>) {
        dataList = newBoards
        notifyDataSetChanged()
    }

    fun getItem(position: Int): BoardModel {
        return dataList[position]
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}
