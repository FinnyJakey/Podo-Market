import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.podomarket.R
import com.example.podomarket.model.BoardModel

class ProductRecyclerViewAdapter(private var dataList: List<BoardModel>, private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<ProductRecyclerViewAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.product_title)
        val userNameTextView: TextView = itemView.findViewById(R.id.product_user_name)
        val priceTextView: TextView = itemView.findViewById(R.id.product_price)
        val productImageView: ImageView = itemView.findViewById(R.id.product_image)

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

        // Glide를 사용하여 이미지 표시
        Glide.with(holder.itemView.context)
            .load(item.pictures[0]) // 첫 번째 이미지 가져오기
            .into(holder.productImageView)
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
