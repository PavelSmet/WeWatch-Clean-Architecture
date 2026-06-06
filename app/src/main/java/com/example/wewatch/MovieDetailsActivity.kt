package com.example.wewatch

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.wewatch.databinding.ActivityMovieDetailsBinding
import com.example.wewatch.domain.model.Movie
import com.example.wewatch.viewmodels.MovieDetailsContract
import com.example.wewatch.viewmodels.MovieDetailsViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailsBinding
    private val viewModel: MovieDetailsViewModel by viewModels()
    private var youTubePlayer: YouTubePlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        // Инициализация плеера (он всегда видим, но с нулевой высотой или скрыт через логику)
        lifecycle.addObserver(binding.youtubePlayerView)
        binding.youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(player: YouTubePlayer) {
                youTubePlayer = player
            }
        })

        val movie = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("movie", Movie::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("movie")
        }

        movie?.let { viewModel.sendIntent(MovieDetailsContract.Intent.Initialize(it)) }

        binding.btnTrailer.setOnClickListener {
            viewModel.sendIntent(MovieDetailsContract.Intent.PlayTrailer)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun render(state: MovieDetailsContract.State) {
        state.movie?.let { movie ->
            binding.apply {
                tvTitle.text = movie.title
                tvYear.text = movie.year
                tvRating.text = movie.rating?.let { "IMDb: $it" } ?: "Нет рейтинга"
                tvGenre.text = movie.genre
                tvPlot.text = movie.plot ?: "Описание отсутствует"
                tvActors.text = movie.actors
                tvDirector.text = "Режиссер: ${movie.director}"

                Glide.with(this@MovieDetailsActivity)
                    .load(movie.posterUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(ivPoster)
            }
        }

        if (state.isPlayerVisible && state.videoId != null) {
            binding.youtubePlayerView.visibility = View.VISIBLE
            binding.cardPoster.visibility = View.GONE
            binding.btnTrailer.visibility = View.GONE
            // Принудительный запуск
            youTubePlayer?.loadVideo(state.videoId, 0f)
        } else {
            binding.youtubePlayerView.visibility = View.GONE
            binding.cardPoster.visibility = View.VISIBLE
            binding.btnTrailer.visibility = View.VISIBLE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
