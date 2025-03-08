package com.example.musicplayer_vaibhav.musicPlayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.asFlow
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import com.example.musicplayer_vaibhav.R
import com.example.musicplayer_vaibhav.musicPlayer.datalayer.models.Song
import com.example.musicplayer_vaibhav.musicPlayer.viewmodel.MusicPlayerViewModel
import com.example.musicplayer_vaibhav.networklayer.DataState
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@Composable
fun MusicPlayerScreen(isInPiPMode: Boolean, viewModel: MusicPlayerViewModel) {

    val songs by viewModel.songs.collectAsState(initial = emptyList())
    val currentSong by viewModel.currentSong.collectAsState(initial = null)
    val currentSongIndex by viewModel.currentSongIndex.collectAsState(0)
    val isPlaying by viewModel.isPlaying.collectAsState(initial = true)
    val songsResponse by viewModel.songsResponse.asFlow()
        .collectAsState(initial = DataState.Loading)

    val context = LocalContext.current
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }

    var isPipModeToggled by remember { mutableStateOf(false) }
    LaunchedEffect(isInPiPMode) {
        //  to handle the toggle between pip mode to disable scrolling to 0
        if (!isPipModeToggled && isInPiPMode) {
            isPipModeToggled = true
        }
    }
    LaunchedEffect(currentSong) {
        currentSong?.downloadUrl?.getOrNull(3)?.url?.let { url ->
            val mediaItem = androidx.media3.common.MediaItem.fromUri(url)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            viewModel.playMusic(context)
        }
    }
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            exoPlayer?.play()
        } else {
            exoPlayer?.pause()
        }
    }
    // Change the UI on basis of Success, Error or Loading State
    when (songsResponse) {
        is DataState.Success -> {
            // Handle isInPipMode UI changes automatically when toggled
            if (!isInPiPMode) {
                MusicPlayerSuccessState(
                    Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFFFF5E62), Color.Black)
                            )
                        ),
                    songs, isPlaying, currentSongIndex,
                    exoPlayer,
                    onPlayPauseClicked = { viewModel.togglePlayPause() },
                    onShuffleClicked = { viewModel.shuffleSongs() },
                    onRewindClicked = {
                        exoPlayer?.seekTo(0)
                        exoPlayer?.play()
                    },
                    onNextClicked = { viewModel.nextClicked() },
                    onPreviousClicked = { viewModel.previousClicked() },
                    changeSongScroll = {
                        if (!isPipModeToggled) {
                            viewModel.selectSong(it)
                        } else {
                            isPipModeToggled = false
                        }

                    }
                )
            } else {
                MiniPlayer(
                    Modifier.background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFFF5E62), Color.Black)
                        )
                    ), currentSong
                )
            }
        }

        is DataState.Error -> {
            Column {
                ErrorView((songsResponse as DataState.Error).errorMessage, {
                    viewModel.fetchSongs(context)
                }) {
                    // enabled this in case the api fail this can be used for the testing purpose
                    viewModel.fetchLocal(context)
                }
            }

        }

        DataState.Loading -> {
            LoadingView()
        }

    }

    LaunchedEffect(Unit) {
        viewModel.fetchSongs(context)
    }

}

@Composable
fun MusicPlayerSuccessState(
    modifier: Modifier = Modifier,
    songs: List<Song>,
    isPlaying: Boolean,
    currentSongIndex: Int = 0,
    exoPlayer: ExoPlayer?,
    onPlayPauseClicked: () -> Unit,
    onShuffleClicked: () -> Unit,
    onRewindClicked: () -> Unit,
    onNextClicked: () -> Unit,
    onPreviousClicked: () -> Unit,
    changeSongScroll: (Int) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {

        // Carousel of Songs
        SongCarousel(
            Modifier
                .fillMaxWidth()
                .weight(1f), songs = songs, currentSongIndex, changeSongScroll
        )

        // song detail of current Playing song added
        songs.getOrNull(currentSongIndex)?.name?.let {
            SongDetails(
                it,
                Modifier
                    .padding(horizontal = 16.dp)
                    .height(100.dp)
                    .fillMaxWidth()

            )
        }
        MusicSeekBar(player = exoPlayer, modifier = Modifier.fillMaxWidth())
        // Controls to handle the navigations and seek
        Controls(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            isPlaying = isPlaying,
            onPlayPauseClicked = onPlayPauseClicked,
            onShuffleClicked = onShuffleClicked,
            onRewindClicked = onRewindClicked,
            onNextClicked = onNextClicked,
            onPreviousClicked = onPreviousClicked,
        )
    }
}

@Composable
fun SongCarousel(
    modifier: Modifier,
    songs: List<Song>,
    currentSongIndex: Int,
    onSongChanged: (Int) -> Unit
) {
    val listState = rememberLazyListState()

    var isUserScrolling by remember { mutableStateOf(false) }
    var lastVisibleIndex by remember { mutableStateOf(currentSongIndex) }

    // Detect scroll state: Is the user actively scrolling?
    LaunchedEffect(listState.isScrollInProgress) {
        snapshotFlow { listState.isScrollInProgress }
            .collect { isScrolling ->
                isUserScrolling = isScrolling
            }
    }

    // When scrolling stops, check which item is centered
    LaunchedEffect(isUserScrolling) {
        if (!isUserScrolling) {
            // Get the current centered index
            val centerIndex = listState.firstVisibleItemIndex // Adjust based on layout
            if (centerIndex != lastVisibleIndex && centerIndex in songs.indices) {
                lastVisibleIndex = centerIndex
                onSongChanged(centerIndex) // Notify parent to update song
            }
        }
    }
    LaunchedEffect(currentSongIndex) {
        if (currentSongIndex != lastVisibleIndex) {
            listState.animateScrollToItem(currentSongIndex)
        }
    }
    LazyRow(
        modifier = modifier,
        state = listState,
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(songs) { index, song ->
            SongItem(
                Modifier
                    .fillMaxHeight(.6f)
                    .fillParentMaxWidth()
                    .padding(horizontal = 32.dp)
                    .clip(RoundedCornerShape(16.dp)), song
            )
        }
    }
}

@Composable
fun SongItem(modifier: Modifier, song: Song) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.black)
        ),
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            model = song.image?.getOrNull(2)?.url,
        )
    }
}

@Composable
fun SongDetails(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = modifier // Get actual text width
    )
}

@Composable
fun MusicSeekBar(
    player: ExoPlayer?,
    modifier: Modifier = Modifier
) {
    player?.let {
        var position by remember { mutableStateOf(0L) }
        var duration by remember { mutableStateOf(0L) }
        var isSeeking by remember { mutableStateOf(false) }

        // Update the slider position every second
        LaunchedEffect(player) {
            while (true) {
                if (!isSeeking) {
                    position = player.currentPosition
                    duration = player.duration.coerceAtLeast(0L) // Avoid negative duration
                }
                delay(1000) // Update every second
            }
        }

        Column(
            modifier = modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Slider(
                value = position.toFloat(),
                valueRange = 0f..duration.toFloat(),
                onValueChange = {
                    isSeeking = true
                    position = it.toLong()
                },
                onValueChangeFinished = {
                    player.seekTo(position)
                    isSeeking = false
                },
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.Red,
                    inactiveTrackColor = Color.Gray
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatTime(position), color = Color.White, fontSize = 12.sp)
                Text(formatTime(duration), color = Color.White, fontSize = 12.sp)
            }
        }
    }

}

@Composable
fun Controls(
    modifier: Modifier,
    isPlaying: Boolean,
    onPlayPauseClicked: () -> Unit,
    onShuffleClicked: () -> Unit,
    onRewindClicked: () -> Unit,
    onNextClicked: () -> Unit,
    onPreviousClicked: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        IconButton(onClick = onShuffleClicked) {
            AsyncImage(
                model = R.drawable.shuffle,
                contentDescription = "Shuffle"
            )
        }

        IconButton(onClick = {
            onPreviousClicked()
        }) {
            AsyncImage(
                model = R.drawable.previous,
                contentDescription = "previous",
            )
        }

        IconButton(onClick = {
            onPlayPauseClicked()
        }) {
            AsyncImage(
                model = if (isPlaying == true) R.drawable.pause else R.drawable.play_icon,
                contentDescription = "Play/Pause",
            )
        }
        IconButton(onClick = {
            onNextClicked()
        }) {
            AsyncImage(
                model = R.drawable.forward,
                contentDescription = "Next",
            )
        }
        IconButton(onClick = onRewindClicked) {
            AsyncImage(
                model = R.drawable.replay,
                contentDescription = "replay",
                colorFilter = ColorFilter.tint(Color.Red)
            )
        }

    }

}

@Composable
fun ErrorView(errorMessage: String?, onRetry: () -> Unit, fetchLocal: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = errorMessage ?: "Something went wrong!",
            color = Color.Red
        )
        Button(
            modifier = Modifier
                .padding(top = 12.dp)
                .wrapContentSize(),
            onClick = onRetry,
        ) {
            Text(
                text = "Retry",
                color = Color.White
            )
        }

        Button(
            modifier = Modifier
                .padding(top = 12.dp)
                .wrapContentSize(),
            onClick = fetchLocal,
        ) {
            Text(
                text = "Fetch Local Json for testing",
                color = Color.White
            )
        }
    }
}


@Composable
fun LoadingView() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun MiniPlayer(modifier: Modifier, song: Song?) {
    song?.let {
        SongItem(
            modifier, song
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMusicPlayerScreen() {

    var song = Song(
        name = "Song 1",
        id = null,
        type = null,
        year = null,
        releaseDate = null,
        duration = null,
        label = null,
        explicitContent = null,
        playCount = null,
        language = null,
        hasLyrics = null,
        lyricsId = null,
        url = null,
        copyright = null,
        album = null,
        artists = null,
        featured = null,
        all = null,
        image = null,
        downloadUrl = null
    )
    MusicPlayerSuccessState(
        Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Red, Color.Black)
                )
            ), listOf(
            song, song, song
        ), true, 0, null, {}, {}, {}, {}, {}, {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewSongCarousel() {
    var song = Song(
        name = "Song 1",
        id = null,
        type = null,
        year = null,
        releaseDate = null,
        duration = null,
        label = null,
        explicitContent = null,
        playCount = null,
        language = null,
        hasLyrics = null,
        lyricsId = null,
        url = null,
        copyright = null,
        album = null,
        artists = null,
        featured = null,
        all = null,
        image = null,
        downloadUrl = null
    )
    SongCarousel(
        Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(bottom = 32.dp),
        songs = listOf(
            song, song, song
        ),
        0
    ) {}
}

@Preview
@Composable
fun SongItemPreview() {
    var song = Song(
        name = "Song 1",
        id = null,
        type = null,
        year = null,
        releaseDate = null,
        duration = null,
        label = null,
        explicitContent = null,
        playCount = null,
        language = null,
        hasLyrics = null,
        lyricsId = null,
        url = null,
        copyright = null,
        album = null,
        artists = null,
        featured = null,
        all = null,
        image = null,
        downloadUrl = null
    )
    SongItem(
        Modifier
            .fillMaxHeight(.6f)
            .padding(8.dp)
            .fillMaxWidth()
            .height(150.dp)
            .background(Color.White), song
    )
}

fun formatTime(ms: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(ms)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
    return String.format("%02d:%02d", minutes, seconds)
}

