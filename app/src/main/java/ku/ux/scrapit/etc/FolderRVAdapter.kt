package ku.ux.scrapit.etc

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import ku.ux.scrapit.data.Folder
import ku.ux.scrapit.databinding.ItemFolderBinding

class FolderRVAdapter(list : RealmList<Folder>) : RecyclerView.Adapter<FolderRVAdapter.ViewHolder>() {

    private val folderList = list
    private var isEditMode = false
    private var isAllChecked = false
    private val checkedItemList = HashSet<Int>()

    inner class ViewHolder(private val binding : ItemFolderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pos : Int) {
            val folder = folderList[pos]
            Log.d("isoo", "bind: ${folder?.nickname}")

            if(folder!!.isDeleted) binding.root.visibility = View.GONE
            else binding.root.visibility = View.VISIBLE
            if(!folder.isFavorites) binding.folderFavoritesIv.visibility = View.INVISIBLE
            else binding.folderFavoritesIv.visibility = View.VISIBLE

            binding.folderIconTv.backgroundTintList = ColorStateList.valueOf(Color.parseColor(folder.color))
            binding.folderTitleTv.text = folder.nickname
            binding.folderDescriptionTv.text = folder.description

            if(isEditMode) {
                binding.folderSwitcher.visibility = View.INVISIBLE
                binding.folderCheckbox.visibility = View.VISIBLE
                binding.folderCheckbox.isChecked = isAllChecked
            } else {
                binding.folderCheckbox.isChecked = false
                binding.folderSwitcher.visibility = View.VISIBLE
                binding.folderCheckbox.visibility = View.INVISIBLE
            }

            binding.folderCheckbox.setOnCheckedChangeListener { _, b ->
                if(b) checkedItemList.add(folder.folderId)
                else checkedItemList.remove(folder.folderId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() : Int = folderList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    fun turnOnEditMode() {
        isEditMode = true
        notifyDataSetChanged()
    }

    fun turnOffEditMode() {
        isEditMode = false
        notifyDataSetChanged()
    }

    fun isCheckAllItem(isCheck : Boolean) {
        isAllChecked = isCheck
        notifyDataSetChanged()
    }

    fun getCheckedScraps() : List<Int> {
        return checkedItemList.toList()
    }

}