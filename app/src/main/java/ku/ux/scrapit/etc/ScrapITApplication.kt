package ku.ux.scrapit.etc

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import io.realm.Realm
import io.realm.RealmConfiguration
import ku.ux.scrapit.data.Folder
import ku.ux.scrapit.data.Scrap

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

//        testStackFragment()
//        testDel()
    }

    private fun testFolderTree() {

    }

    private fun testDel() {
        // Realm 인스턴스 얻기
        val realm = Realm.getDefaultInstance()

        // Realm 트랜잭션 시작
        realm.executeTransaction {
            // 모든 폴더 삭제
            it.where(Folder::class.java).findAll().deleteAllFromRealm()

            // 모든 스크랩 삭제
            it.where(Scrap::class.java).findAll().deleteAllFromRealm()
        }

        // Realm 사용 종료
        realm.close()
    }

    private fun testStackFragment() {
        // Realm 인스턴스 얻기
        val realm = Realm.getDefaultInstance()

        // 폴더 생성 및 속성 설정
        val folder = Folder()
        folder.folderId = 1
        folder.nickname = "폴더 1"
        folder.description = "폴더 1의 설명"
        folder.color = "#008000"
        folder.isFavorites = true

        // 폴더에 속한 스크랩 생성 및 속성 설정
        val scrap1 = Scrap()
        scrap1.scrapId = 1
        scrap1.nickname = "스크랩 1"
        scrap1.url = "https://www.google.com"
        scrap1.description = "스크랩 1의 설명"
        scrap1.color = "#008000"
        scrap1.isFavorites = true

        val scrap2 = Scrap()
        scrap2.scrapId = 2
        scrap2.nickname = "스크랩 2"
        scrap2.url = "https://www.google.com"
        scrap2.description = "스크랩 2의 설명"
        scrap2.color = "#008000"
        scrap2.isFavorites = true

        // 폴더 생성 및 속성 설정
        val folder2 = Folder()
        folder.folderId = 2
        folder.nickname = "폴더 1"
        folder.description = "폴더 1의 설명"
        folder.color = "#008000"
        folder.isFavorites = true

        // 폴더에 속한 스크랩 생성 및 속성 설정
        val scrap3 = Scrap()
        scrap1.scrapId = 3
        scrap1.nickname = "스크랩 1"
        scrap1.url = "https://www.google.com"
        scrap1.description = "스크랩 1의 설명"
        scrap1.color = "#008000"
        scrap1.isFavorites = true

        val scrap4 = Scrap()
        scrap2.scrapId = 4
        scrap2.nickname = "스크랩 2"
        scrap2.url = "https://www.google.com"
        scrap2.description = "스크랩 2의 설명"
        scrap2.color = "#008000"
        scrap2.isFavorites = true

        // 폴더에 스크랩 추가
        folder.scrapList.add(scrap1)
        folder.scrapList.add(scrap2)
        folder2.scrapList.add(scrap3)
        folder2.scrapList.add(scrap4)

        // Realm 트랜잭션 시작
        realm.executeTransaction {
            // 폴더와 하위 스크랩들을 Realm에 추가
            it.copyToRealmOrUpdate(folder)
            it.copyToRealmOrUpdate(folder2)
        }

        // Realm 사용 종료
        realm.close()
    }

}