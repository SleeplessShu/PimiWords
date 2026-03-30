package com.sleeplessdog.pimi.trash.trash

/*
@Composable
fun DictionaryNavGraph(
    navController: NavHostController,
    viewModel: DictionaryViewModel,
) {

    val state by viewModel.categoriesGrouped.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "dictionary_main"
    ) {

        composable("dictionary_main") {

            DictionaryScreen(
                userGroups = state.userGroups,
                standardGroups = state.globalGroups,
                onNavigateToUserGroup = { groupId ->
                    navController.navigate("user_group/$groupId")
                },
                onNavigateToGlobalGroup = { groupId ->
                    navController.navigate("global_group/$groupId")
                },
                addNewUserGroup = viewModel::addNewUserGroup
            )
        }

        composable(
            "user_group/{groupId}"
        ) { backStackEntry ->

            val groupId =
                backStackEntry.arguments?.getString("groupId")!!

            UserGroupScreen(
                groupId = groupId,
                navController = navController
            )
        }

        composable(
            "global_group/{groupId}"
        ) { backStackEntry ->

            val groupId =
                backStackEntry.arguments?.getString("groupId")!!

            UserGroupScreen(
                groupId = groupId,
                navController = navController
            )
        }
    }
}*/
