package com.example.quicknotes

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var currentPathTextView: TextView
    private lateinit var backButton: ImageButton
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var menuButton: ImageButton
    var folderStack = mutableListOf<Int?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        val language = getLocalePreferences()
        setLocale(this, language)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        window.statusBarColor = resources.getColor(R.color.primary)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        menuButton = findViewById(R.id.menuButton)
        currentPathTextView = findViewById(R.id.currentPathTextView)
        backButton = findViewById(R.id.backButton)
        menuButton.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        backButton.setOnClickListener {
            onBackPressed()
        }

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_font_size -> showFontChangeDialog()
                R.id.menu_language -> showLanguageChangeDialog()
                R.id.menu_about -> showAboutAuthorsDialog()
            }
            true
        }

        val darkThemeSwitch = navView.menu.findItem(R.id.menu_dark_theme).actionView as SwitchCompat
        val isDarkThemeEnabled = getThemePreferences()
        darkThemeSwitch.isChecked = isDarkThemeEnabled
        if (isDarkThemeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        darkThemeSwitch.isChecked =
            AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        darkThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            saveThemePreference(isChecked)
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainFragment())
                .commit()
        }
    }

    private fun showAboutAuthorsDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_about, null)

        MaterialAlertDialogBuilder(this).setView(dialogView)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
            }.create().show()
    }

    private fun showLanguageChangeDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_select_language, null)
        val radioGroup: RadioGroup = dialogView.findViewById(R.id.radioGroup)

        MaterialAlertDialogBuilder(this).setView(dialogView)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                val language = when (radioGroup.checkedRadioButtonId) {
                    R.id.englishRadioButton -> "en"
                    R.id.russianRadioButton -> "ru"
                    else -> "en"
                }
                setLocale(this, language)
                saveLocalePreferences(language)
            }.setNegativeButton(getString(R.string.cancel), null).create().show()
    }

    private fun showFontChangeDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_select_font_size, null)

        MaterialAlertDialogBuilder(this).setView(dialogView)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
            }.setNegativeButton(getString(R.string.cancel), null).create().show()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment is MainFragment) {
            if (folderStack.isNotEmpty()) {
                folderStack.removeAt(folderStack.size - 1)
                val lastFolderId = folderStack.lastOrNull()
                currentFragment.noteViewModel.loadItems(lastFolderId)
                updateCurrentPath()
                updateBackButtonVisibility()
            }
        } else if (currentFragment is NoteDetailFragment) {
            val noteId = currentFragment.arguments?.getInt("noteId")
            val folderStack = currentFragment.arguments?.getIntegerArrayList("folderStack")
            if (folderStack != null) {
                this.folderStack.clear()
                this.folderStack.addAll(folderStack)
            }
            if (noteId != null) {
                currentFragment.viewModel.getNoteById(noteId).observe(this) { note ->
                    if (note != null) {
                        folderStack?.add(note.folderId)
                        updateCurrentPath()
                        updateBackButtonVisibility()
                        supportFragmentManager.popBackStack()
                    }
                }
            }
        }
    }

    fun updateCurrentPath() {
        currentPathTextView.text = folderStack.lastOrNull()?.let { folderId ->
            if (folderId == null) "Root" else {
                val mainFragment =
                    supportFragmentManager.findFragmentById(R.id.fragment_container) as MainFragment
                ""
            }
        }
    }

    fun updateBackButtonVisibility() {
        backButton.visibility = if (folderStack.isEmpty() || folderStack.lastOrNull() == null) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun saveThemePreference(isDarkTheme: Boolean) {
        val sharedPreference = getSharedPreferences("theme_prefs", MODE_PRIVATE)
        sharedPreference.edit().putBoolean("dark_theme", isDarkTheme).apply()
    }

    private fun saveLocalePreferences(language: String) {
        val sharedPreference = getSharedPreferences("locale_prefs", MODE_PRIVATE)
        sharedPreference.edit().putString("language", language).apply()
    }

    private fun getThemePreferences(): Boolean {
        val sharedPreferences = getSharedPreferences("theme_prefs", MODE_PRIVATE)
        return sharedPreferences.getBoolean("dark_theme", false)
    }

    private fun getLocalePreferences(): String? {
        val sharedPreferences = getSharedPreferences("locale_prefs", MODE_PRIVATE)
        return sharedPreferences.getString("language", "en")
    }

    private fun setLocale(context: Context, language: String?) {
        val newLocale = Locale(language)
        Locale.setDefault(newLocale)

        val configuration = context.resources.configuration
        configuration.setLocale(newLocale)
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)

        if (context is AppCompatActivity) {
            val currentLanguage = getLocalePreferences()
            if (currentLanguage != language) {
                context.recreate()
            }
        }
    }
}