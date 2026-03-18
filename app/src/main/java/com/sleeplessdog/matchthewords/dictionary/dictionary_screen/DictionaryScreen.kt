package com.sleeplessdog.matchthewords.dictionary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.backend.domain.models.GroupUiDictionary
import com.sleeplessdog.matchthewords.dictionary.authorisation.DictionarySyncState
import com.sleeplessdog.matchthewords.dictionary.authorisation.toImageRes
import com.sleeplessdog.matchthewords.dictionary.authorisation.toSummaryImageRes
import com.sleeplessdog.matchthewords.dictionary.dialogs.DeletingDialog
import com.sleeplessdog.matchthewords.dictionary.dialogs.GroupDialog
import com.sleeplessdog.matchthewords.dictionary.dictionary_screen.DictionaryViewModel
import com.sleeplessdog.matchthewords.dictionary.models.DialogType
import com.sleeplessdog.matchthewords.ui.theme.BlackPrimary
import com.sleeplessdog.matchthewords.ui.theme.DarkTextDefault
import com.sleeplessdog.matchthewords.ui.theme.Gray03
import com.sleeplessdog.matchthewords.ui.theme.GreenPrimary
import com.sleeplessdog.matchthewords.ui.theme.White
import com.sleeplessdog.matchthewords.ui.theme.textSize20Medium
import com.sleeplessdog.matchthewords.ui.theme.textSize24Medium

@Composable
fun DictionaryUi(
    viewModel: DictionaryViewModel,
    onNavigateToUserGroup: (String, String) -> Unit,
    onNavigateToGlobalGroup: (String, String) -> Unit,
    bottomPadding: Int,
) {
    val state by viewModel.groupState.collectAsState()

    DictionaryScreen(
        viewModel = viewModel,
        userGroups = state.userGroups,
        standardGroups = state.globalGroups,
        onNavigateToUserGroup = onNavigateToUserGroup,
        onNavigateToGlobalGroup = onNavigateToGlobalGroup,
        addNewUserGroup = viewModel::addNewUserGroup,
        onRefresh = viewModel::refreshGroups,
        bottomPadding = bottomPadding,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryScreen(
    viewModel: DictionaryViewModel,
    userGroups: List<GroupUiDictionary>,
    standardGroups: List<GroupUiDictionary>,
    onNavigateToUserGroup: (String, String) -> Unit,
    onNavigateToGlobalGroup: (String, String) -> Unit,
    addNewUserGroup: (String) -> Unit,
    onRefresh: () -> Unit,
    bottomPadding: Int,
) {
    var renameGroup by remember { mutableStateOf<Pair<String, String>?>(null) }
    var deleteGroup by remember { mutableStateOf<Pair<String, String>?>(null) }
    var showSyncOverlay by remember { mutableStateOf(false) }
    val syncState by viewModel.syncState.collectAsState()
    val isRefreshing by viewModel.isGroupsRefreshing.collectAsState()

    renameGroup?.let { (key, title) ->
        GroupDialog(
            onDismiss = { renameGroup = null },
            onConfirm = { newName ->
                viewModel.renameGroup(key, newName)
                renameGroup = null
            },
            dialogType = DialogType.RENAME_GROUP,
            groupTitle = title
        )
    }

    deleteGroup?.let { (key, title) ->
        DeletingDialog(
            onDismiss = { deleteGroup = null },
            onConfirm = {
                viewModel.deleteGroup(key)
                deleteGroup = null
            },
            title = title,
            dialogType = DialogType.DELETE_GROUP
        )
    }

    if (showSyncOverlay) {
        SyncStateOverlay(
            syncState = syncState,
            onRefresh = {
                onRefresh()
                showSyncOverlay = false
            },
            onDismiss = { showSyncOverlay = false }
        )
    }

    Column(
        modifier = Modifier
            .background(BlackPrimary)
    ) {
        HeaderDictionary(
            syncState = syncState,
            onOpenOverlay = { showSyncOverlay = true }
        )

        Spacer(modifier = Modifier.height(8.dp))

        DictionaryTabs(
            userGroups = userGroups,
            globalGroups = standardGroups,
            onNavigateToUserGroup = onNavigateToUserGroup,
            onNavigateToGlobalGroup = onNavigateToGlobalGroup,
            onRenameGroup = { key, title -> renameGroup = key to title },
            onDeleteGroup = { key, title -> deleteGroup = key to title },
            addNewUserGroup = addNewUserGroup,
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            bottomPadding = bottomPadding
        )
    }
}

@Composable
fun HeaderDictionary(
    syncState: DictionarySyncState,
    onOpenOverlay: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Icon(
            painter = painterResource(syncState.toSummaryImageRes()),
            tint = White,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 20.dp)
                .size(28.dp)
                .clickable { onOpenOverlay() }
        )

        Text(
            text = stringResource(R.string.dictionary),
            style = textSize24Medium,
            color = DarkTextDefault,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncStateOverlay(
    syncState: DictionarySyncState,
    onRefresh: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BlackPrimary,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.sync_overlay_title),
                style = textSize24Medium,
                color = DarkTextDefault,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            SyncStateRow(
                iconRes = syncState.auth.toImageRes(),
                label = stringResource(R.string.sync_label_auth)
            )

            SyncStateRow(
                iconRes = syncState.globalDb.toImageRes(),
                label = stringResource(R.string.sync_label_global_db)
            )

            SyncStateRow(
                iconRes = syncState.userDb.toImageRes(),
                label = stringResource(R.string.sync_label_user_db)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onRefresh,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary,
                    contentColor = BlackPrimary
                )
            ) {
                Text(
                    text = stringResource(R.string.sync_refresh),
                    style = textSize20Medium
                )
            }
        }
    }
}

@Composable
private fun SyncStateRow(iconRes: Int, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconRes),
            tint = White,
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = textSize20Medium,
            color = DarkTextDefault
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryTabs(
    userGroups: List<GroupUiDictionary>,
    globalGroups: List<GroupUiDictionary>,
    onNavigateToUserGroup: (String, String) -> Unit,
    onNavigateToGlobalGroup: (String, String) -> Unit,
    addNewUserGroup: (String) -> Unit,
    onRenameGroup: (String, String) -> Unit,
    onDeleteGroup: (String, String) -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    bottomPadding: Int,
) {
    var selectedTab by rememberSaveable {
        mutableIntStateOf(GroupsTab.USERS.ordinal)
    }

    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                divider = {},

                indicator = {
                    TabRowDefaults.Indicator(
                        modifier = Modifier
                            .tabIndicatorOffset(selectedTabIndex = selectedTab)
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        height = 3.dp,
                        color = GreenPrimary
                    )
                }
            ) {
                GroupsTab.entries.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        selectedContentColor = White,
                        unselectedContentColor = Gray03,
                        text = {
                            Text(
                                style = textSize20Medium,
                                text = tab.label
                            )
                        }
                    )
                }
            }

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.weight(1f)
            ) {
                when (selectedTab) {

                    GroupsTab.USERS.ordinal -> {
                        UserGroupsTable(
                            groups = userGroups,
                            onNavigateToUserGroup = onNavigateToUserGroup,
                            onRenameGroup = onRenameGroup,
                            onDeleteGroup = onDeleteGroup,
                        )
                    }

                    GroupsTab.GLOBAL.ordinal -> {
                        StandardGroupsTable(
                            groups = globalGroups,
                            onNavigateToGlobalGroup = onNavigateToGlobalGroup,
                            bottomPadding = bottomPadding
                        )
                    }
                }
            }
        }

        if (selectedTab == GroupsTab.USERS.ordinal) {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = DarkTextDefault,
                contentColor = BlackPrimary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = 16.dp,
                        bottom = 120.dp
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_add),
                    contentDescription = "Add group"
                )
            }
        }

        if (showDialog) {
            GroupDialog(
                onDismiss = { showDialog = false },
                onConfirm = {
                    addNewUserGroup(it)
                    showDialog = false
                },
                dialogType = DialogType.NEW_GROUP
            )
        }
    }
}
