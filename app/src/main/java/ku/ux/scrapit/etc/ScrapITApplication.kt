package ku.ux.scrapit.etc

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import io.realm.Realm
import io.realm.RealmConfiguration
import ku.ux.scrapit.data.Folder

class ScrapITApplication : Application() {

    companion object {
        lateinit var rootFolder : Folder
        var currentFolderId = -1

        @SuppressLint("CommitPrefEdits")
        fun setCurrentFolderId(context : Context, id : Int) {
            val pref = context.getSharedPreferences("storage", Context.MODE_PRIVATE)
            pref.edit().putInt("currentFolderId", id)
        }
    }

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        val config : RealmConfiguration = RealmConfiguration.Builder()
            .allowWritesOnUiThread(true)
            .name("scrapIT.realm")
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)

        val pref = applicationContext.getSharedPreferences("storage", Context.MODE_PRIVATE)
        currentFolderId = pref.getInt("currentFolderId", -1)

        val realm = Realm.getDefaultInstance()
        val rootFolderId = -1
        rootFolder = realm.where(Folder::class.java).equalTo("folderId", rootFolderId).findFirst() ?: Folder()
        if(rootFolder.nickname == "") {
            rootFolder.nickname = "루트 폴더"
            realm.beginTransaction()
            realm.copyToRealmOrUpdate(rootFolder)
            realm.commitTransaction()
        }
    }

}