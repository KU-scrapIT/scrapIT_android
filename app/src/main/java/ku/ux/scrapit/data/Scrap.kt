package ku.ux.scrapit.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable

open class Scrap : RealmObject() {
    @PrimaryKey
    var scrapId = 0
    var nickname : String = ""
    var url : String = ""
    var description : String = ""
    var color : String = IndexColor.BLUE.colorCode
    var isFavorites : Boolean = false
    var isDeleted : Boolean = false
    var parentFolder : Folder? = null
    var localPath : String = "" // local pdf file path. type can be changed
}
