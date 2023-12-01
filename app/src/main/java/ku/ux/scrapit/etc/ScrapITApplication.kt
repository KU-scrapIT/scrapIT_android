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
        private var newFolderIdCounter = 0
        private var newScrapIdCounter = 0

        fun generateNewIFolderId(): Int {
            newFolderIdCounter += 1
            return newFolderIdCounter
        }
        fun generateNewIScrapId(): Int {
            newScrapIdCounter += 1
            return newScrapIdCounter
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
            realm.beginTransaction()
            rootFolder.nickname = "루트 폴더"
            realm.copyToRealmOrUpdate(rootFolder)
            realm.commitTransaction()
        }

        testStackFragment()
        //testDel()
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
        folder.nickname = "폴더 11"
        folder.description = "폴더 1의 설명"
        folder.color = "#88C8FF"
        folder.isFavorites = true

        // 폴더에 속한 스크랩 생성 및 속성 설정
        val scrap1 = Scrap()
        scrap1.scrapId = 1
        scrap1.nickname = "스크랩 1"
        scrap1.url = "https://translate.google.co.kr/"
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
        folder2.folderId = 2
        folder2.nickname = "폴더 22"
        folder2.description = "폴더 2의 설명"
        folder2.color = "#C1A5FF"
        folder2.isFavorites = true

        // 폴더에 속한 스크랩 생성 및 속성 설정
        val scrap3 = Scrap()
        scrap3.scrapId = 3
        scrap3.nickname = "스크랩 1"
        scrap3.url = "https://www.google.com"
        scrap3.description = "스크랩 3의 설명"
        scrap3.color = "#008000"
        scrap3.isFavorites = true

        val scrap4 = Scrap()
        scrap4.scrapId = 4
        scrap4.nickname = "스크랩 2"
        scrap4.url = "https://translate.google.co.kr/"
        scrap4.description = "스크랩 4의 설명"
        scrap4.color = "#008000"
        scrap4.isFavorites = true

        // 폴더 생성 및 속성 설정
        val folder3 = Folder()
        folder3.folderId = 3
        folder3.nickname = "폴더 33"
        folder3.description = "폴더 3의 설명"
        folder3.color = "#C1A5FF"
        folder3.isFavorites = false

        // 폴더 생성 및 속성 설정
        val folder4 = Folder()
        folder4.folderId = 4
        folder4.nickname = "폴더 44"
        folder4.description = "폴더 4의 설명"
        folder4.color = "#C1A5FF"
        folder4.isFavorites = false

        // 폴더에 속한 스크랩 생성 및 속성 설정
        val scrap5 = Scrap()
        scrap5.scrapId = 5
        scrap5.nickname = "스크랩 5"
        scrap5.url = "https://translate.google.co.kr/"
        scrap5.description = "스크랩 5의 설명"
        scrap5.color = "#008000"
        scrap5.isFavorites = true

        val scrap6 = Scrap()
        scrap6.scrapId = 6
        scrap6.nickname = "스크랩 2"
        scrap6.url = "https://translate.google.co.kr"
        scrap6.description = "스크랩 6의 설명"
        scrap6.color = "#008000"
        scrap6.isFavorites = true

        // 폴더에 스크랩 추가
        folder.scrapList.add(scrap1)
        folder.scrapList.add(scrap2)
        folder2.scrapList.add(scrap3)
        folder2.scrapList.add(scrap4)
        folder3.scrapList.add(scrap5)
        folder3.scrapList.add(scrap6)
        folder3.childFolderList.add(folder4)
        folder4.parentFolder = folder3

        folder.parentFolder = rootFolder
        folder2.parentFolder = rootFolder
        folder3.parentFolder = rootFolder

        realm.beginTransaction()
        rootFolder.childFolderList.add(folder)
        rootFolder.childFolderList.add(folder2)
        rootFolder.childFolderList.add(folder3)
        realm.commitTransaction()

        // Realm 트랜잭션 시작
        realm.executeTransaction {
            // 폴더와 하위 스크랩들을 Realm에 추가
            it.copyToRealmOrUpdate(folder)
            it.copyToRealmOrUpdate(folder2)
            it.copyToRealmOrUpdate(folder3)
            it.copyToRealmOrUpdate(folder4)
        }

        // Realm 사용 종료
        realm.close()
    }

}