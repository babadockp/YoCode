class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var languagesAdapter: LanguagesAdapter
    private lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsManager = SettingsManager(this)
        applyTheme()
        applyLanguage()
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupNavigationDrawer()
        loadProgrammingLanguages()
        setupSearchView()
        showWelcomeDialog()
    }

    private fun applyTheme() {
        when (settingsManager.getTheme()) {
            Theme.LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Theme.DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            Theme.SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun applyLanguage() {
        val locale = when (settingsManager.getLanguage()) {
            Language.FRENCH -> Locale("fr")
            Language.ENGLISH -> Locale("en")
            Language.SYSTEM -> Locale.getDefault()
        }
        updateLocale(locale)
    }

    private fun showSettingsDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.settings)
            .setItems(R.array.settings_options) { _, which ->
                when (which) {
                    0 -> showThemeDialog()
                    1 -> showLanguageDialog()
                }
            }
            .show()
    }

    private fun showThemeDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.choose_theme)
            .setSingleChoiceItems(
                R.array.theme_options,
                settingsManager.getTheme().ordinal
            ) { dialog, which ->
                settingsManager.setTheme(Theme.values()[which])
                recreate()
                dialog.dismiss()
            }
            .show()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "YoCode"
            subtitle = "by YoYo - Codez comme un pro"
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupNavigationDrawer() {
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> showUserProfile()
                R.id.nav_progress -> showLearningProgress()
                R.id.nav_achievements -> showAchievements()
                R.id.nav_settings -> showSettings()
            }
            binding.drawerLayout.closeDrawers()
            true
        }
    }

    private fun setupSearchView() {
        binding.searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    filterLanguages(query)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    filterLanguages(newText)
                    return true
                }
            })
        }
    }

    private fun filterLanguages(query: String?) {
        val filteredList = if (query.isNullOrEmpty()) {
            originalLanguagesList
        } else {
            originalLanguagesList.filter { language ->
                language.name.contains(query, ignoreCase = true) ||
                language.description.contains(query, ignoreCase = true)
            }
        }
        languagesAdapter.updateList(filteredList)
    }

    private fun showWelcomeDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Bienvenue sur YoCode!")
            .setMessage("Choisissez votre niveau pour commencer :")
            .setPositiveButton("Débutant") { _, _ ->
                filterByDifficulty(Difficulty.BEGINNER)
            }
            .setNegativeButton("Avancé") { _, _ ->
                filterByDifficulty(Difficulty.ADVANCED)
            }
            .setNeutralButton("Tous les niveaux") { _, _ ->
                loadProgrammingLanguages()
            }
            .show()
    }

    private fun filterByDifficulty(difficulty: Difficulty) {
        val filteredList = originalLanguagesList.filter { 
            it.difficulty == difficulty 
        }
        languagesAdapter.updateList(filteredList)
    }

    private fun loadProgrammingLanguages() {
        originalLanguagesList = listOf(
            ProgrammingLanguage(
                name = "Python",
                description = "Le meilleur langage pour débuter",
                iconResId = R.drawable.ic_python,
                difficulty = Difficulty.BEGINNER,
                progress = 0,
                totalLessons = 20,
                features = listOf("Data Science", "Web", "AI", "Automation"),
                learningPath = listOf(
                    LearningModule("Introduction", listOf(
                        "Variables et types",
                        "Structures de contrôle",
                        "Fonctions"
                    )),
                    LearningModule("Intermédiaire", listOf(
                        "Classes et objets",
                        "Gestion des erreurs",
                        "Modules et packages"
                    ))
                )
            ),
            ProgrammingLanguage(
                name = "Java",
                description = "Langage orienté objet populaire pour Android",
                iconResId = R.drawable.ic_java,
                difficulty = Difficulty.INTERMEDIATE
            ),
            ProgrammingLanguage(
                name = "C++",
                description = "Langage puissant pour la programmation système",
                iconResId = R.drawable.ic_cpp,
                difficulty = Difficulty.ADVANCED
            ),
            ProgrammingLanguage(
                name = "Rust",
                description = "Langage moderne pour des systèmes performants et sûrs",
                iconResId = R.drawable.ic_rust,
                difficulty = Difficulty.ADVANCED
            ),
            ProgrammingLanguage(
                name = "JavaScript",
                description = "Le langage du web par excellence",
                iconResId = R.drawable.ic_javascript,
                difficulty = Difficulty.BEGINNER
            )
        )
        languagesAdapter.updateList(originalLanguagesList)
    }

    private fun showUserProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    private fun showLearningProgress() {
        startActivity(Intent(this, ProgressActivity::class.java))
    }

    private fun showAchievements() {
        startActivity(Intent(this, AchievementsActivity::class.java))
    }

    companion object {
        private lateinit var originalLanguagesList: List<ProgrammingLanguage>
    }
} 