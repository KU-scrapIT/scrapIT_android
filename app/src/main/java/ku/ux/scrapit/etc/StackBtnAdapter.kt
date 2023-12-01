package ku.ux.scrapit.etc

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import ku.ux.scrapit.activity.StackActivity
import ku.ux.scrapit.data.Folder
import ku.ux.scrapit.databinding.ItemStacksBinding

class StackBtnAdapter(private val folderNames: List<String>, private val folderColors: List<String>) :
    RecyclerView.Adapter<StackBtnAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun itemClicked(pos : Int)
    }

    private var itemClickListener : OnItemClickListener? = null

    fun setOnItemClickListener(listener : OnItemClickListener) {
        itemClickListener = listener
    }

    inner class ViewHolder(private val binding: ItemStacksBinding) : RecyclerView.ViewHolder(binding.root) {
        val folderTextView: TextView = binding.stackFoldersTv
        val folderImageButton: ImageButton = binding.stackFoldersIb
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStacksBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.folderTextView.text = folderNames[position]

//        if (position == 0) {
//            val shadowColor = android.graphics.Color.parseColor("#0000FF")
//            holder.folderImageButton.setShadowLayer(10f, 0f, 0f, shadowColor)
//        }

        holder.folderImageButton.setColorFilter(android.graphics.Color.parseColor(folderColors[position]))

        holder.folderImageButton.setOnClickListener {
            itemClickListener?.itemClicked(position)
        }
    }

    override fun getItemCount(): Int {
        return folderNames.size
    }

}