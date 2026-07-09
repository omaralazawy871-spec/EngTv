package com.example.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.example.data.Channel
import com.example.data.Playlist

@Composable
fun IptvDashboard(
    modifier: Modifier = Modifier,
    viewModel: IptvViewModel = viewModel()
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    
    val playlists by viewModel.playlists.collectAsState()
    val channels by viewModel.channels.collectAsState()
    val favoriteChannels by viewModel.favoriteChannels.collectAsState()
    val selectedPlaylist by viewModel.selectedPlaylist.collectAsState()
    val selectedGroup by viewModel.selectedGroup.collectAsState()
    val groupTitles by viewModel.groupTitles.collectAsState()
    val currentPlayingChannel by viewModel.currentPlayingChannel.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val importState by viewModel.importState.collectAsState()
    
    var showImportDialog by remember { mutableStateOf(false) }

    // Always enforce RTL Layout Direction for the Arabic-centric player, or respect system RTL
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (isLandscape) {
            LandscapeLayout(
                playlists = playlists,
                channels = channels,
                favorites = favoriteChannels,
                selectedPlaylist = selectedPlaylist,
                selectedGroup = selectedGroup,
                groupTitles = groupTitles,
                currentPlayingChannel = currentPlayingChannel,
                searchQuery = searchQuery,
                onSelectPlaylist = { viewModel.selectPlaylist(it) },
                onSelectGroup = { viewModel.selectGroup(it) },
                onSearchChanged = { viewModel.setSearchQuery(it) },
                onPlayChannel = { viewModel.playChannel(it) },
                onToggleFavorite = { viewModel.toggleFavorite(it) },
                onDeletePlaylist = { viewModel.deletePlaylist(it) },
                onImportClick = { showImportDialog = true }
            )
        } else {
            PortraitLayout(
                playlists = playlists,
                channels = channels,
                favorites = favoriteChannels,
                selectedPlaylist = selectedPlaylist,
                selectedGroup = selectedGroup,
                groupTitles = groupTitles,
                currentPlayingChannel = currentPlayingChannel,
                searchQuery = searchQuery,
                onSelectPlaylist = { viewModel.selectPlaylist(it) },
                onSelectGroup = { viewModel.selectGroup(it) },
                onSearchChanged = { viewModel.setSearchQuery(it) },
                onPlayChannel = { viewModel.playChannel(it) },
                onToggleFavorite = { viewModel.toggleFavorite(it) },
                onDeletePlaylist = { viewModel.deletePlaylist(it) },
                onImportClick = { showImportDialog = true }
            )
        }

        if (showImportDialog) {
            ImportPlaylistDialog(
                importState = importState,
                onDismiss = {
                    showImportDialog = false
                    viewModel.resetImportState()
                },
                onSubmitUrl = { name, url ->
                    viewModel.addPlaylistFromUrl(name, url)
                },
                onSubmitContent = { name, content ->
                    viewModel.addPlaylistFromContent(name, content)
                }
            )
        }
    }
}

@Composable
fun PortraitLayout(
    playlists: List<Playlist>,
    channels: List<Channel>,
    favorites: List<Channel>,
    selectedPlaylist: Playlist?,
    selectedGroup: String?,
    groupTitles: List<String>,
    currentPlayingChannel: Channel?,
    searchQuery: String,
    onSelectPlaylist: (Playlist?) -> Unit,
    onSelectGroup: (String?) -> Unit,
    onSearchChanged: (String) -> Unit,
    onPlayChannel: (Channel?) -> Unit,
    onToggleFavorite: (Channel) -> Unit,
    onDeletePlaylist: (Playlist) -> Unit,
    onImportClick: () -> Unit
) {
    var activeTab by remember { mutableStateOf(0) } // 0: Playlists, 1: Channels, 2: Favorites

    Column(modifier = Modifier.fillMaxSize()) {
        // App Header
        AppHeader(onImportClick = onImportClick)

        // Player Section (if channel is playing)
        AnimatedVisibility(
            visible = currentPlayingChannel != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            currentPlayingChannel?.let { channel ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    VideoPlayer(
                        streamUrl = channel.url,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                    )
                    PlayingChannelBar(
                        channel = channel,
                        onToggleFavorite = { onToggleFavorite(channel) },
                        onClosePlayer = { onPlayChannel(null) }
                    )
                }
            }
        }

        // Tab Navigation for switching views
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text("القوائم (M3U)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium) }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text("القنوات", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium) }
            )
            Tab(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                text = { Text("المفضلة", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium) }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (activeTab) {
                0 -> PlaylistManagerTab(
                    playlists = playlists,
                    selectedPlaylist = selectedPlaylist,
                    onSelectPlaylist = {
                        onSelectPlaylist(it)
                        activeTab = 1 // Switch to channels automatically
                    },
                    onDeletePlaylist = onDeletePlaylist
                )
                1 -> ChannelBrowserTab(
                    selectedPlaylist = selectedPlaylist,
                    channels = channels,
                    groupTitles = groupTitles,
                    selectedGroup = selectedGroup,
                    currentPlayingChannel = currentPlayingChannel,
                    searchQuery = searchQuery,
                    onSelectGroup = onSelectGroup,
                    onSearchChanged = onSearchChanged,
                    onChannelClick = onPlayChannel,
                    onToggleFavorite = onToggleFavorite,
                    onBackToPlaylists = { activeTab = 0 }
                )
                2 -> FavoritesTab(
                    favorites = favorites,
                    currentPlayingChannel = currentPlayingChannel,
                    onChannelClick = onPlayChannel,
                    onToggleFavorite = onToggleFavorite
                )
            }
        }
    }
}

@Composable
fun LandscapeLayout(
    playlists: List<Playlist>,
    channels: List<Channel>,
    favorites: List<Channel>,
    selectedPlaylist: Playlist?,
    selectedGroup: String?,
    groupTitles: List<String>,
    currentPlayingChannel: Channel?,
    searchQuery: String,
    onSelectPlaylist: (Playlist?) -> Unit,
    onSelectGroup: (String?) -> Unit,
    onSearchChanged: (String) -> Unit,
    onPlayChannel: (Channel?) -> Unit,
    onToggleFavorite: (Channel) -> Unit,
    onDeletePlaylist: (Playlist) -> Unit,
    onImportClick: () -> Unit
) {
    var activeSidebarTab by remember { mutableStateOf(1) } // 0: Playlists, 1: Channels, 2: Favorites

    Row(modifier = Modifier.fillMaxSize()) {
        // Player Section (Left pane, takes dominant space)
        Box(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            if (currentPlayingChannel != null) {
                Column(modifier = Modifier.fillMaxSize()) {
                    VideoPlayer(
                        streamUrl = currentPlayingChannel.url,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                    PlayingChannelBar(
                        channel = currentPlayingChannel,
                        onToggleFavorite = { onToggleFavorite(currentPlayingChannel) },
                        onClosePlayer = { onPlayChannel(null) }
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Tv,
                        contentDescription = "No Stream",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "اختر قناة لبدء البث المباشر بجودة عالية",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Control Sidebar (Right pane)
        Column(
            modifier = Modifier
                .weight(0.8f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Header for Import inside sidebar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "مشغل IPTV",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Button(
                    onClick = onImportClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة M3U", style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Tab Navigation for switching views
            TabRow(
                selectedTabIndex = activeSidebarTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Tab(
                    selected = activeSidebarTab == 0,
                    onClick = { activeSidebarTab = 0 },
                    text = { Text("القوائم", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeSidebarTab == 1,
                    onClick = { activeSidebarTab = 1 },
                    text = { Text("القنوات", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeSidebarTab == 2,
                    onClick = { activeSidebarTab = 2 },
                    text = { Text("المفضلة", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold) }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (activeSidebarTab) {
                    0 -> PlaylistManagerTab(
                        playlists = playlists,
                        selectedPlaylist = selectedPlaylist,
                        onSelectPlaylist = {
                            onSelectPlaylist(it)
                            activeSidebarTab = 1
                        },
                        onDeletePlaylist = onDeletePlaylist
                    )
                    1 -> ChannelBrowserTab(
                        selectedPlaylist = selectedPlaylist,
                        channels = channels,
                        groupTitles = groupTitles,
                        selectedGroup = selectedGroup,
                        currentPlayingChannel = currentPlayingChannel,
                        searchQuery = searchQuery,
                        onSelectGroup = onSelectGroup,
                        onSearchChanged = onSearchChanged,
                        onChannelClick = onPlayChannel,
                        onToggleFavorite = onToggleFavorite,
                        onBackToPlaylists = { activeSidebarTab = 0 }
                    )
                    2 -> FavoritesTab(
                        favorites = favorites,
                        currentPlayingChannel = currentPlayingChannel,
                        onChannelClick = onPlayChannel,
                        onToggleFavorite = onToggleFavorite
                    )
                }
            }
        }
    }
}

@Composable
fun AppHeader(onImportClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Tv,
                    contentDescription = "App Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "مشغل القنوات IPTV",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Button(
                onClick = onImportClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Import List", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "إضافة قائمة",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PlayingChannelBar(
    channel: Channel,
    onToggleFavorite: () -> Unit,
    onClosePlayer: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChannelLogo(
                logoUrl = channel.logo,
                channelName = channel.name,
                modifier = Modifier.size(42.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = channel.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!channel.groupTitle.isNullOrEmpty()) {
                    Text(
                        text = channel.groupTitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (channel.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (channel.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onClosePlayer) {
                Icon(
                    imageVector = Icons.Default.Delete, // Close player or stop stream
                    contentDescription = "Close Player",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistManagerTab(
    playlists: List<Playlist>,
    selectedPlaylist: Playlist?,
    onSelectPlaylist: (Playlist) -> Unit,
    onDeletePlaylist: (Playlist) -> Unit
) {
    if (playlists.isEmpty()) {
        EmptyStateView(
            title = "لا توجد قوائم تشغيل",
            description = "اضغط على زر 'إضافة قائمة' في الأعلى لرفع ملف M3U أو إدخال رابط القنوات الخاص بك."
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "قوائم التشغيل الخاصة بك (${playlists.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            items(playlists) { playlist ->
                val isSelected = selectedPlaylist?.id == playlist.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = { onSelectPlaylist(playlist) },
                            onLongClick = { /* Can implement contextual menu */ }
                        ),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = "Playlist",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = playlist.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (playlist.sourceUrl == "local_file") "ملف محلي مرفوع" else playlist.sourceUrl,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        IconButton(onClick = { onDeletePlaylist(playlist) }) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "حذف القائمة",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChannelBrowserTab(
    selectedPlaylist: Playlist?,
    channels: List<Channel>,
    groupTitles: List<String>,
    selectedGroup: String?,
    currentPlayingChannel: Channel?,
    searchQuery: String,
    onSelectGroup: (String?) -> Unit,
    onSearchChanged: (String) -> Unit,
    onChannelClick: (Channel) -> Unit,
    onToggleFavorite: (Channel) -> Unit,
    onBackToPlaylists: () -> Unit
) {
    if (selectedPlaylist == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = "Folder",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "يرجى اختيار قائمة تشغيل أولاً",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "انتقل إلى علامة تبويب 'القوائم' لتحديد قائمة القنوات الخاصة بك.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onBackToPlaylists) {
                Text("تصفح القوائم M3U")
            }
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("بحث عن قناة...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // Categories / Groups Scrolling row (if searching is empty)
        if (searchQuery.isEmpty() && groupTitles.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    CategoryChip(
                        name = "الكل",
                        isSelected = selectedGroup == "All" || selectedGroup == null,
                        onClick = { onSelectGroup("All") }
                    )
                }
                items(groupTitles) { title ->
                    CategoryChip(
                        name = title,
                        isSelected = selectedGroup == title,
                        onClick = { onSelectGroup(title) }
                    )
                }
            }
        }

        if (channels.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchQuery.isNotEmpty()) "لا توجد قنوات تطابق البحث" else "لا توجد قنوات في هذا القسم",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
        } else {
            // Channel vertical grid / list (adaptive)
            val configuration = LocalConfiguration.current
            val spanCount = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 2 else 1

            LazyVerticalGrid(
                columns = GridCells.Fixed(spanCount),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(channels) { channel ->
                    ChannelCard(
                        channel = channel,
                        isPlaying = currentPlayingChannel?.id == channel.id,
                        onClick = { onChannelClick(channel) },
                        onToggleFavorite = { onToggleFavorite(channel) }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoritesTab(
    favorites: List<Channel>,
    currentPlayingChannel: Channel?,
    onChannelClick: (Channel) -> Unit,
    onToggleFavorite: (Channel) -> Unit
) {
    if (favorites.isEmpty()) {
        EmptyStateView(
            title = "لا توجد قنوات مفضلة",
            description = "انتقل إلى علامة تبويب 'القنوات' واضغط على أيقونة القلب لإضافة قنواتك المفضلة هنا للوصول السريع."
        )
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(favorites) { channel ->
                ChannelCard(
                    channel = channel,
                    isPlaying = currentPlayingChannel?.id == channel.id,
                    onClick = { onChannelClick(channel) },
                    onToggleFavorite = { onToggleFavorite(channel) }
                )
            }
        }
    }
}

@Composable
fun CategoryChip(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun ChannelCard(
    channel: Channel,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChannelLogo(
                logoUrl = channel.logo,
                channelName = channel.name,
                modifier = Modifier.size(50.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = channel.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isPlaying) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!channel.groupTitle.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = channel.groupTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isPlaying) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (channel.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (channel.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun ChannelLogo(
    logoUrl: String?,
    channelName: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (!logoUrl.isNullOrEmpty()) {
            SubcomposeAsyncImage(
                model = logoUrl,
                contentDescription = channelName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
                error = {
                    ChannelFallbackIcon(channelName = channelName)
                }
            )
        } else {
            ChannelFallbackIcon(channelName = channelName)
        }
    }
}

@Composable
fun ChannelFallbackIcon(channelName: String) {
    val initial = if (channelName.isNotEmpty()) channelName.first().toString() else "C"
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun EmptyStateView(title: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Tv,
            contentDescription = "Empty State Icon",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ImportPlaylistDialog(
    importState: ImportState,
    onDismiss: () -> Unit,
    onSubmitUrl: (String, String) -> Unit,
    onSubmitContent: (String, String) -> Unit
) {
    var isUrlMode by remember { mutableStateOf(true) }
    var playlistName by remember { mutableStateOf("") }
    var playlistUrl by remember { mutableStateOf("") }
    var playlistContent by remember { mutableStateOf("") }

    // Close on success
    LaunchedEffect(importState) {
        if (importState is ImportState.Success) {
            onDismiss()
        }
    }

    Dialog(onDismissRequest = { if (importState !is ImportState.Loading) onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 400.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "إضافة قائمة قنوات M3U",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Toggle tabs: URL or Copy-Paste Text
                TabRow(
                    selectedTabIndex = if (isUrlMode) 0 else 1,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = isUrlMode,
                        onClick = { isUrlMode = true },
                        text = { Text("رابط ويب (URL)") }
                    )
                    Tab(
                        selected = !isUrlMode,
                        onClick = { isUrlMode = false },
                        text = { Text("لصق ملف M3U") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Input Name
                OutlinedTextField(
                    value = playlistName,
                    onValueChange = { playlistName = it },
                    label = { Text("اسم القائمة (مثال: قنواتي)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (isUrlMode) {
                    // Input URL
                    OutlinedTextField(
                        value = playlistUrl,
                        onValueChange = { playlistUrl = it },
                        label = { Text("رابط ملف M3U الذكي") },
                        placeholder = { Text("http://example.com/playlist.m3u") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                } else {
                    // Input Paste Text
                    OutlinedTextField(
                        value = playlistContent,
                        onValueChange = { playlistContent = it },
                        label = { Text("محتوى ملف M3U النصي") },
                        placeholder = { Text("#EXTM3U\n#EXTINF:-1,اسم القناة\nhttp://stream-url.com") },
                        minLines = 4,
                        maxLines = 6,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Status indication
                when (importState) {
                    is ImportState.Loading -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("جاري معالجة وتحميل القنوات...", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    is ImportState.Error -> {
                        Text(
                            text = importState.error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    else -> {}
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        enabled = importState !is ImportState.Loading
                    ) {
                        Text("إلغاء")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (isUrlMode) {
                                onSubmitUrl(playlistName, playlistUrl)
                            } else {
                                onSubmitContent(playlistName, playlistContent)
                            }
                        },
                        enabled = importState !is ImportState.Loading && playlistName.isNotBlank() && (if (isUrlMode) playlistUrl.isNotBlank() else playlistContent.isNotBlank()),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("حفظ واستيراد")
                    }
                }
            }
        }
    }
}
