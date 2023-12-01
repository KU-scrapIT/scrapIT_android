package ku.ux.scrapit.etc

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginStart
import androidx.recyclerview.widget.RecyclerView
import ku.ux.scrapit.R
import ku.ux.scrapit.data.Folder
import ku.ux.scrapit.databinding.ItemFolderBinding
import ku.ux.scrapit.databinding.ItemFolderTreeBinding

class FolderTreeRVAdapter(rootFolder : Folder) : RecyclerView.Adapter<FolderTreeRVAdapter.ViewHolder>() {

    inner class FolderItem(
        val folder : Folder,
        val depth : Int,
        var isOpen : Boolean
    )

    private val folderItemList = ArrayList<FolderItem>()

    init {
        folderItemList.add(FolderItem(rootFolder, 0, false))
    }

    inner class ViewHolder(private val binding : ItemFolderTreeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pos : Int) {
            val folderItem = folderItemList[pos]
            binding.itemFolderTreeBtn.iconTint = ColorStateList.valueOf(Color.parseColor(folderItem.folder.color))
            binding.itemFolderTreeBtn.text = folderItem.folder.nickname

            val layoutParams = binding.itemFolderTreeBtn.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.marginStart = 30 * folderItem.depth
            binding.itemFolderTreeBtn.layoutParams = layoutParams

            binding.itemFolderTreeBtn.setOnClickListener {
                itemClicked(pos)
            }

            if(folderItem.isOpen) binding.itemFolderTreeArrow.setImageResource(R.drawable.down_arrow)
            else binding.itemFolderTreeArrow.setImageResource(R.drawable.right_arrow)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFolderTreeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = folderItemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    private fun itemClicked(pos : Int) {
        if(folderItemList[pos].isOpen) {
            val end = folderItemList[pos].folder.childFolderList.size
            for (i in 1..end) {
                folderItemList.removeAt(pos + i)
            }
        } else {
            val end = folderItemList[pos].folder.childFolderList.size
            for (i in 1..end) {
                folderItemList.add(pos + i,
                    FolderItem(folderItemList[pos].folder.childFolderList[i-1]!!,
                        folderItemList[pos].depth + 1, false))
            }
        }
        folderItemList[pos].isOpen = !folderItemList[pos].isOpen
        notifyItemChanged(pos)
        Log.d("isoo", "itemClicked: ${folderItemList[pos].isOpen}")
        if(folderItemList[pos].isOpen)
            notifyItemRangeInserted(pos + 1, folderItemList[pos].folder.childFolderList.size)
        else
            notifyItemRangeRemoved(pos + 1, folderItemList[pos].folder.childFolderList.size)
    }

}