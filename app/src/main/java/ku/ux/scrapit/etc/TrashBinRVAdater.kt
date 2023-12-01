package ku.ux.scrapit.etc

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmList
import ku.ux.scrapit.data.Folder
import ku.ux.scrapit.data.Scrap
import ku.ux.scrapit.databinding.ItemFolderBinding
import ku.ux.scrapit.databinding.ItemScrapBinding

class TrashBinRVAdapter(private val scrapList: MutableList<Scrap>,
                        private val folderList: MutableList<Folder>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        // 뷰 타입 상수 정의
        private const val VIEW_TYPE_FOLDER = 1
        private const val VIEW_TYPE_SCRAP = 2
    }

    private val checkedItemList = HashSet<Int>()
    private var isAllChecked = false

    inner class FolderViewHolder(private val binding : ItemFolderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pos : Int) {
            val folder = folderList[pos]
            //삭제 체크된것만 나타내기
            if(folder!!.isDeleted) binding.root.visibility = View.VISIBLE
            else binding.root.visibility = View.GONE

            if(!folder.isFavorites) binding.folderFavoritesIv.visibility = View.INVISIBLE
            else binding.folderFavoritesIv.visibility = View.VISIBLE

            binding.folderIconTv.backgroundTintList = ColorStateList.valueOf(Color.parseColor(folder.color))
            binding.folderTitleTv.text = folder.nickname
            binding.folderDescriptionTv.text = folder.description

            //굳이 edit 모드가 아니어도 체크 표시가 떠있어야 하니 따로 설정
            binding.folderSwitcher.visibility = View.INVISIBLE
            binding.folderCheckbox.visibility = View.VISIBLE
            binding.folderCheckbox.isChecked = isAllChecked

            //체크 표시 여부
            binding.folderCheckbox.setOnCheckedChangeListener { _, b ->
                if(b) checkedItemList.add(folder.folderId)
                else checkedItemList.remove(folder.folderId)
            }
        }
    }

    inner class ScrapViewHolder(private val binding : ItemScrapBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pos : Int) {

            val scrap = scrapList[pos]
            //삭제 체크된것만 나타내기
            if(scrap!!.isDeleted) binding.root.visibility = View.VISIBLE
            else binding.root.visibility = View.GONE
            
            if(!scrap.isFavorites) binding.scrapFavoritesIv.visibility = View.INVISIBLE
            else binding.scrapFavoritesIv.visibility = View.VISIBLE

            if(scrap.nickname.isNotEmpty())
                binding.scrapIconTv.text = scrap.nickname[0].toString()
            else
                binding.scrapIconTv.text = ""

            binding.scrapIconTv.backgroundTintList = ColorStateList.valueOf(Color.parseColor(scrap.color))
            binding.scrapTitleTv.text = scrap.nickname
            binding.scrapDescriptionTv.text = scrap.description

            //굳이 edit 모드가 아니어도 체크 표시가 떠있어야 하니 따로 설정
            binding.scrapSwitcher.visibility = View.INVISIBLE
            binding.scrapCheckbox.visibility = View.VISIBLE
            binding.scrapCheckbox.isChecked = isAllChecked

            //체크 표시 여부
            binding.scrapCheckbox.setOnCheckedChangeListener { _, b ->
                if(b) checkedItemList.add(scrap.scrapId)
                else checkedItemList.remove(scrap.scrapId)
            }
        }
    }

    fun selectAll(){

        isAllChecked = true
        checkedItemList.clear() // 기존에 선택된 아이템 초기화

        // 데이터를 반복하며 모든 아이템을 선택
        for (folder in folderList) {
            checkedItemList.add(folder.folderId)
        }

        for (scrap in scrapList) {
            checkedItemList.add(scrap.scrapId)
        }

        notifyDataSetChanged() // 어댑터에게 데이터 변경을 알림

//        isAllChecked=true
//        notifyDataSetChanged()
    }

    fun deleteChecked() {   //  체크된거 삭제
        var deleteThings = checkedItemList
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        for(it in deleteThings){
            val folderToDelete = realm.where(Folder::class.java)
                .equalTo("folderId", it) // 삭제할 조건 지정
                .findFirst()
            folderToDelete?.deleteFromRealm() // 찾은 객체 삭제
            checkedItemList.remove(it)
        }
        for(it in deleteThings){
            val folderToDelete = realm.where(Scrap::class.java)
                .equalTo("ScrapId", it) // 삭제할 조건 지정
                .findFirst()
            folderToDelete?.deleteFromRealm() // 찾은 객체 삭제
        }
        realm.close()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_FOLDER -> {
                val binding = ItemFolderBinding.inflate(inflater, parent, false)
                FolderViewHolder(binding)
            }
            VIEW_TYPE_SCRAP -> {
                val binding = ItemScrapBinding.inflate(inflater, parent, false)
                ScrapViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_FOLDER -> {
                val folderViewHolder = holder as FolderViewHolder
                folderViewHolder.bind(position)
            }
            VIEW_TYPE_SCRAP -> {
                val scrapViewHolder = holder as ScrapViewHolder
                scrapViewHolder.bind(position)
            }
        }
    }

    override fun getItemCount() : Int = scrapList.size + folderList.size

    override fun getItemViewType(position: Int): Int {
        return if (position < folderList.size) {
            VIEW_TYPE_FOLDER
        } else {
            VIEW_TYPE_SCRAP
        }
    }
}