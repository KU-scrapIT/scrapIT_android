package ku.ux.scrapit.etc

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import ku.ux.scrapit.data.Scrap
import ku.ux.scrapit.databinding.ItemScrapBinding

class ScrapRVAdapter(private val list : RealmList<Scrap>) : RecyclerView.Adapter<ScrapRVAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding : ItemScrapBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pos : Int) {
            val scrap = list[pos]

            if(scrap!!.isDeleted) binding.root.visibility = View.GONE
            else binding.root.visibility = View.VISIBLE
            if(!scrap.isFavorites) binding.scrapFavoritesIv.visibility = View.INVISIBLE
            else binding.scrapFavoritesIv.visibility = View.VISIBLE

            binding.scrapIconTv.text = scrap.nickname[0].toString()
            binding.scrapIconTv.backgroundTintList = ColorStateList.valueOf(Color.parseColor(scrap.color))
            binding.scrapTitleTv.text = scrap.nickname
            binding.scrapDescriptionTv.text = scrap.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemScrapBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

}