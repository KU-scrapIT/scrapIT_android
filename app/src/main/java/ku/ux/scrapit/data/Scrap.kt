package ku.ux.scrapit.data

data class Scrap(
    var nickname : String,
    var url : String,
    var description : String,
    var color : IndexColor,
    var isFavorites : Boolean,
    var isDeleted : Boolean,
    var parentFolder : Folder,
    var localPath : String? // local pdf file path. type can be changed
)
