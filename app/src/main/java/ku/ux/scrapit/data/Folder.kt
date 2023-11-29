package ku.ux.scrapit.data

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable

open class Folder : RealmObject() {
    @PrimaryKey
    var folderId = -1
    var nickname : String = ""
    var description : String = ""
    var color : String = IndexColor.BLUE.colorCode
    var isFavorites : Boolean = false
    var isDeleted : Boolean = false
    var parentFolder : Folder?= null
    var childFolderList = RealmList<Folder>()
    var scrapList = RealmList<Scrap>()
}
