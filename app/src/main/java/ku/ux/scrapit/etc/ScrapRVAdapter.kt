package ku.ux.scrapit.etc

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmList
import ku.ux.scrapit.data.Scrap
import ku.ux.scrapit.databinding.ItemScrapBinding
import kotlin.math.log

class ScrapRVAdapter(val list : MutableList<Scrap>) : RecyclerView.Adapter<ScrapRVAdapter.ViewHolder>(), ItemTouchHelperListener {

    private val scrapList = ArrayList<Scrap>()
    private var isEditMode = false
    private var isAllChecked = false
    private val checkedItemList = HashSet<Int>()

    init {
        for(scrap in list) {
            if(!scrap.isDeleted) {
                scrapList.add(scrap)
            }
        }
    }

    interface OnClickListener {
        fun onClick(pos : Int)
    }

    private var onClickListener : OnClickListener? = null

    fun setOnClickListener(listener : OnClickListener) {
        onClickListener = listener
    }

    inner class ViewHolder(private val binding : ItemScrapBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pos : Int) {
            val scrap = scrapList[pos]

            if(scrap!!.isDeleted) binding.root.visibility = View.GONE
            else binding.root.visibility = View.VISIBLE
            if(!scrap.isFavorites) binding.scrapFavoritesIv.visibility = View.INVISIBLE
            else binding.scrapFavoritesIv.visibility = View.VISIBLE

            if(scrap.nickname.isNotEmpty())
                binding.scrapIconTv.text = scrap.nickname[0].toString()
            else
                binding.scrapIconTv.text = ""

            binding.scrapIconTv.backgroundTintList = ColorStateList.valueOf(Color.parseColor(scrap.color))
            binding.scrapTitleTv.text = scrap.nickname
            binding.scrapDescriptionTv.text = scrap.description

            if(isEditMode) {
                binding.scrapSwitcher.visibility = View.INVISIBLE
                binding.scrapCheckbox.visibility = View.VISIBLE
                binding.scrapCheckbox.isChecked = isAllChecked
            } else {
                binding.scrapCheckbox.isChecked = false
                binding.scrapSwitcher.visibility = View.VISIBLE
                binding.scrapCheckbox.visibility = View.INVISIBLE
            }

            binding.scrapCheckbox.setOnCheckedChangeListener { _, b ->
                if(b) checkedItemList.add(scrap.scrapId)
                else checkedItemList.remove(scrap.scrapId)
            }

            binding.root.setOnClickListener {
                if(!isEditMode) onClickListener?.onClick(pos)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemScrapBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() : Int = scrapList.size

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
        checkedItemList.clear()
        if(isAllChecked) {
            for(scrap in scrapList) {
                checkedItemList.add(scrap.scrapId)
            }
        }
        notifyDataSetChanged()
    }

    fun updateList() {
        scrapList.clear()
        for(scrap in list) {
            if(!scrap.isDeleted) {
                scrapList.add(scrap)
            }
        }
    }

    fun getCheckedScraps() : List<Int> {
        return checkedItemList.toList()
    }

    override fun onItemMove(from: Int, to: Int) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val item: Scrap = list[from]
        list.removeAt(from)
        list.add(to, item)
        realm.commitTransaction()
        notifyItemMoved(from, to)
    }

}