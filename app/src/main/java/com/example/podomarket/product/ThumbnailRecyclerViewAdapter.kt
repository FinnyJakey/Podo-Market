import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.podomarket.R
import java.io.File

class ThumbnailRecyclerViewAdapter(private var stringList: List<String>, private var FileList: List<File>, private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<ThumbnailRecyclerViewAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productThumbnailImage: ImageView = itemView.findViewById(R.id.product_thumbnail_image)
        val layout: ConstraintLayout = itemView.findViewById<ConstraintLayout>(R.id.product_thumbnail_layout)
        val deleteButton: CardView = itemView.findViewById<CardView>(R.id.delete_button)
        init {
            deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(position,stringList.size)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.product_thumbnail_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val stringListSize = stringList.size
        if(position < stringListSize){
            val item = stringList[position]
            holder.deleteButton.bringToFront()
            Glide.with(holder.itemView.context)
                .load(item)
                .into(holder.productThumbnailImage)
        }else {
            val item = FileList[position-stringListSize]
            holder.deleteButton.bringToFront()
            Glide.with(holder.itemView.context)
                .load(item)
                .into(holder.productThumbnailImage)
        }
    }

    override fun getItemCount(): Int {
        return stringList.size + FileList.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, stringListSize: Int)
    }
}
