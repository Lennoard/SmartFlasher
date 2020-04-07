package com.smartpack.smartflasher

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.tabs.TabLayout
import com.smartpack.smartflasher.fragments.AboutFragment
import com.smartpack.smartflasher.fragments.BackupFragment
import com.smartpack.smartflasher.fragments.FlasherFragment
import com.smartpack.smartflasher.utils.KernelUpdater
import com.smartpack.smartflasher.utils.PagerAdapter
import com.smartpack.smartflasher.utils.Prefs.getBoolean
import com.smartpack.smartflasher.utils.UpdateCheck
import com.smartpack.smartflasher.utils.Utils
import com.smartpack.smartflasher.utils.root.RootUtils
import kotlinx.android.synthetic.main.activity_main.*

/**
 * How MainActivity would look like if Kotlin
 * (Not used anywhere)
 */
class MainActivityKt : AppCompatActivity() {
    private var mExit = false
    private val mHandler : Handler by lazy { // init the variable only when it's first used somewhere
        Handler()
    }

    override fun onCreate(savedInstanceState: Bundle?) { // Initialize App Theme & Google Ads
        Utils.initializeAppTheme(this)
        Utils.getInstance().initializeGoogleAds(this)
        super.onCreate(savedInstanceState)
        // Set App Language
        Utils.setLanguage(this)
        setContentView(R.layout.activity_main)

        val unsupported = findViewById<AppCompatImageView>(R.id.no_root_Image)
        val textView = findViewById<TextView>(R.id.no_root_Text)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayoutID)

        /*
            We can use kotlin android extensions to do all the binding.
            Here using this viewpager as example for the rest of the code.
            See line 62 and 65
        */

        //val viewPager = findViewById<ViewPager>(R.id.viewPagerID)

        if (!RootUtils.rootAccess()) {
            // Direct property access, no "setText()"
            textView.text = getString(R.string.no_root)
            unsupported.setImageDrawable(resources.getDrawable(R.drawable.ic_help))
            return
        }

        // Use "object.apply { }" or with(object) { } to move to a dedicated context (this)
        // This block creates an instance of PageAdapter with some properties already set
        // and at the end sets itself (this) as the adapter of viewPager
        PagerAdapter(supportFragmentManager).apply {
            AddFragment(FlasherFragment(), getString(R.string.flasher))
            AddFragment(BackupFragment(), getString(R.string.backup))
            AddFragment(AboutFragment(), getString(R.string.about))
            viewPagerID.adapter = this // auto binding the view

        }

        tabLayout.setupWithViewPager(viewPagerID)
        // Usage example of the extension property of type "Context"
        tabLayout.setBackgroundColor(colorPrimary)
        // Usage example of the extension function of type "Context"
        tabLayout.setBackgroundColor(getThemeAccentColor())
    }

    fun androidRooting(view: View?) {
        Utils.launchUrl("https://www.google.com/search?site=&source=hp&q=android+rooting+magisk", this)
    }

    public override fun onStart() {
        super.onStart()
        if (!Utils.checkWriteStoragePermission(this)) {
            return
        }
        if (Utils.networkUnavailable(this)) {
            return
        }
        if (!Utils.isDownloadBinaries()) {
            return
        }
        // Initialize kernel update check - Once in a day
        if (getBoolean("update_check", true, this)
                && KernelUpdater.getUpdateChannel() != "Unavailable" && KernelUpdater.lastModified() +
                89280000L < System.currentTimeMillis()) {
            KernelUpdater.updateInfo(Utils.readFile(Utils.getInternalDataStorage() + "/update_channel"))
        }
        // Initialize manual Update Check, if play store not found
        if (!UpdateCheck.isPlayStoreInstalled(this)) {
            UpdateCheck.autoUpdateCheck(this)
        }
    }

    override fun onBackPressed() {
        if (RootUtils.rootAccess()) {
            if (mExit) {
                mExit = false
                super.onBackPressed()
            } else {
                Utils.toast(R.string.press_back, this)
                mExit = true
                mHandler.postDelayed({ mExit = false }, 2000)
            }
        } else {
            super.onBackPressed()
        }
    }
}