package com.example.wewatch

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wewatch.databinding.ActivityMainBinding
import com.example.wewatch.domain.model.Movie
import com.example.wewatch.viewmodels.MainContract
import com.example.wewatch.viewmodels.MainViewModel
import com.example.wewatch.views.adapters.MovieAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: MovieAdapter

    private val searchActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val movie = result.data?.getParcelableExtra<Movie>("selected_movie")
            movie?.let { viewModel.sendIntent(MainContract.Intent.AddMovie(it)) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = MovieAdapter(
            movies = emptyList(),
            onItemClick = { movie ->
                viewModel.sendIntent(MainContract.Intent.MovieClicked(movie))
            },
            onSelectionChange = { movie, isSelected ->
                viewModel.sendIntent(MainContract.Intent.UpdateMovieSelection(movie, isSelected))
            }
        )

        binding.rvMovies.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupClickListeners() {
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            searchActivityResultLauncher.launch(intent)
        }
        binding.btnDelete.setOnClickListener { showDeleteConfirmationDialog() }
        binding.btnCancel.setOnClickListener { viewModel.sendIntent(MainContract.Intent.ClearSelection) }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { state -> render(state) }
                }
                launch {
                    // Используем collectLatest для эффектов
                    viewModel.effect.collectLatest { effect -> handleEffect(effect) }
                }
            }
        }
    }

    private fun render(state: MainContract.State) {
        binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        
        if (state.movies.isEmpty() && !state.isLoading) {
            binding.rvMovies.visibility = View.GONE
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.selectionBar.visibility = View.GONE
        } else {
            binding.rvMovies.visibility = View.VISIBLE
            binding.emptyStateLayout.visibility = View.GONE
            adapter.updateMovies(state.movies)
            
            if (state.isSelectionMode) {
                binding.selectionBar.visibility = View.VISIBLE
                binding.fabAdd.hide()
            } else {
                binding.selectionBar.visibility = View.GONE
                binding.fabAdd.show()
            }
        }
    }

    private fun handleEffect(effect: MainContract.Effect) {
        when (effect) {
            is MainContract.Effect.NavigateToDetails -> {
                val intent = Intent(this, MovieDetailsActivity::class.java).apply {
                    putExtra("movie", effect.movie)
                }
                startActivity(intent)
            }
            is MainContract.Effect.ShowError -> Toast.makeText(this, effect.message, Toast.LENGTH_LONG).show()
            is MainContract.Effect.ShowToast -> Toast.makeText(this, effect.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Удаление")
            .setMessage("Удалить выбранные фильмы?")
            .setPositiveButton("Да") { _, _ -> viewModel.sendIntent(MainContract.Intent.DeleteSelectedMovies) }
            .setNegativeButton("Нет", null)
            .show()
    }
}
