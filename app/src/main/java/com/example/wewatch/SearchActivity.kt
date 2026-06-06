package com.example.wewatch

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wewatch.databinding.ActivitySearchBinding
import com.example.wewatch.viewmodels.SearchContract
import com.example.wewatch.viewmodels.SearchViewModel
import com.example.wewatch.views.adapters.MovieAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupSearchButton()
        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = MovieAdapter(
            movies = emptyList(),
            onItemClick = { movie ->
                val resultIntent = Intent().apply {
                    putExtra("selected_movie", movie)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            },
            onSelectionChange = { _, _ -> }
        )
        binding.rvSearchResults.layoutManager = LinearLayoutManager(this)
        binding.rvSearchResults.adapter = adapter
    }

    private fun setupSearchButton() {
        binding.btnSearch.setOnClickListener {
            val query = binding.etSearch.text.toString()
            val year = binding.etYear.text.toString().takeIf { it.isNotBlank() }
            
            if (query.isNotEmpty()) {
                viewModel.sendIntent(SearchContract.Intent.SearchMovies(query, year))
            } else {
                Toast.makeText(this, "Введите название фильма", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { state ->
                        render(state)
                    }
                }
                launch {
                    viewModel.effect.collect { effect ->
                        handleEffect(effect)
                    }
                }
            }
        }
    }

    private fun render(state: SearchContract.State) {
        binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        
        adapter.updateMovies(state.searchResults)
        
        if (state.isLoading) {
            binding.tvEmpty.visibility = View.GONE
            binding.rvSearchResults.visibility = View.GONE
        } else {
            if (state.error != null) {
                binding.tvEmpty.text = state.error
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvSearchResults.visibility = View.GONE
            } else if (state.searchResults.isEmpty()) {
                binding.tvEmpty.text = "Введите данные для поиска"
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvSearchResults.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.rvSearchResults.visibility = View.VISIBLE
            }
        }
    }

    private fun handleEffect(effect: SearchContract.Effect) {
        when (effect) {
            is SearchContract.Effect.ShowToast -> {
                Toast.makeText(this, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
