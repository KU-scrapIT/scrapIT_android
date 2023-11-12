package ku.ux.scrapit.data

data class Folder(
    var nickname : String,
    var description : String,
    var color : IndexColor,
    var isFavorites : Boolean,
    var isDeleted : Boolean,
    var parentFolder : Folder?,
    var childFolderList : ArrayList<Folder>,
    var scrapList : ArrayList<Scrap>
)
