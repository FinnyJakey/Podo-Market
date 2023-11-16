import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.podomarket.R
import com.example.podomarket.common.CommonUtil
import com.example.podomarket.model.BoardModel

class ThumbnailRecyclerViewAdapter(private var dataList: List<String>, private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<ThumbnailRecyclerViewAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productThumbnailImage: ImageView = itemView.findViewById(R.id.product_thumbnail_image)

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
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.product_thumbnail_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = dataList[position]

        Glide.with(holder.itemView.context)
            .load(item)
            .into(holder.productThumbnailImage)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}
