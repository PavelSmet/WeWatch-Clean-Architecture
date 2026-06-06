package com.example.wewatch

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.wewatch.databinding.ActivityAddMovieBinding
import com.example.wewatch.domain.model.Movie
import com.example.wewatch.viewmodels.AddMovieContract
import com.example.wewatch.viewmodels.AddMovieViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddMovieActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMovieBinding
    private val viewModel: AddMovieViewModel by viewModels()

    private val searchLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val selectedMovie = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                data?.getParcelableExtra("selected_movie", Movie::class.java)
            } else {
                @Suppress("DEPRECATION")
                data?.getParcelableExtra("selected_movie")
            }

            selectedMovie?.let { 
                viewModel.sendIntent(AddMovieContract.Intent.MovieSelected(it)) 
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.btnSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            searchLauncher.launch(intent)
        }

        binding.btnAdd.setOnClickListener {
            viewModel.sendIntent(AddMovieContract.Intent.AddMovieClicked)
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

    private fun render(state: AddMovieContract.State) {
        state.movie?.let { movie ->
            binding.etTitle.setText(movie.title)
            binding.etYear.setText(movie.year)

            Glide.with(this)
                .load(movie.posterUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(binding.ivPoster)
            
            binding.ivPoster.visibility = View.VISIBLE
        }
        binding.btnAdd.isEnabled = state.isButtonEnabled
    }

    private fun handleEffect(effect: AddMovieContract.Effect) {
        when (effect) {
            is AddMovieContract.Effect.FinishWithResult -> {
                val intent = Intent()
                intent.putExtra("selected_movie", effect.movie)
                setResult(RESULT_OK, intent)
                finish()
            }
            is AddMovieContract.Effect.ShowToast -> {
                Toast.makeText(this, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
