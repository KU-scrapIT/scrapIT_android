package ku.ux.scrapit.etc

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ku.ux.scrapit.databinding.ItemStacksBinding

class StackBtnAdapter(private val folderId: List<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder(private val binding: ItemStacksBinding) : RecyclerView.ViewHolder(binding.root) {
        val folderTextView: TextView = binding.stackFoldersTv
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStacksBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val folderTitle = folderId[position]
            holder.folderTextView.text = folderTitle
        }
    }

    override fun getItemCount(): Int {
        return folderId.size
    }
}