package ku.ux.scrapit.etc

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class ScrapITApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        val config : RealmConfiguration = RealmConfiguration.Builder()
            .allowWritesOnUiThread(true)
            .name("scrapIT.realm")
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)



    }

}