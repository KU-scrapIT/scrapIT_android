package ku.ux.scrapit.etc

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import ku.ux.scrapit.activity.StackActivity
import ku.ux.scrapit.data.Folder
import ku.ux.scrapit.databinding.ItemStacksBinding

class StackBtnAdapter(private val folderNames: List<String>) :
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
        val folderImageView: ImageView = binding.stackFoldersIb
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStacksBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("isoo", "onBindViewHolder: $position")
        holder.folderTextView.text = folderNames[position]

        holder.itemView.setOnClickListener {
            itemClickListener?.itemClicked(position)
        }
    }

    override fun getItemCount(): Int {
        return folderNames.size
    }

}