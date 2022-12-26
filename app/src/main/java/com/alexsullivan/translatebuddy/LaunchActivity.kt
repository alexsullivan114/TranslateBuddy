package com.alexsullivan.translatebuddy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.alexsullivan.translatebuddy.grouping.CreateWordGroupScreen
import com.alexsullivan.translatebuddy.grouping.TranslationGroupingScreen
import com.alexsullivan.translatebuddy.list.TranslationListScreen
import com.alexsullivan.translatebuddy.work.DownloadTranslationsWork
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import java.util.concurrent.TimeUnit

class LaunchActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      val navController = rememberNavController()
      Scaffold(
        bottomBar = {
          BottomNavigation {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val items = listOf(TopLevelScreen.List, TopLevelScreen.Grouping)
            items.forEach { screen ->
              BottomNavigationItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(stringResource(screen.stringId)) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                  navController.navigate(screen.route) {
                    // Pop up to the start destination of the graph to
                    // avoid building up a large stack of destinations
                    // on the back stack as users select items
                    popUpTo(navController.graph.findStartDestination().id) {
                      saveState = true
                    }
                    // Avoid multiple copies of the same destination when
                    // reselecting the same item
                    launchSingleTop = true
                    // Restore state when reselecting a previously selected item
                    restoreState = true
                  }
                }
              )
            }
          }
        }
      ) { innerPadding ->
        NavHost(navController, startDestination = TopLevelScreen.List.route, Modifier.padding(innerPadding)) {
          composable(TopLevelScreen.List.route) { TranslationListScreen(navController) }
          composable(TopLevelScreen.Grouping.route) { TranslationGroupingScreen(navController) }
          composable(
            Screen.AddGrouping.route,
            arguments = Screen.AddGrouping.namedArguments
          ) {
            val wordGroupId = it.arguments?.getString(Screen.AddGrouping.arguments[0])
            CreateWordGroupScreen(navController, wordGroupId)
          }
        }
      }
    }

    signIn()
  }

  private fun isUserSignedIn(): Boolean {
    val account = GoogleSignIn.getLastSignedInAccount(this)
    return account != null
  }
  private fun signIn() {
    if (!isUserSignedIn()) {
      val gso = GoogleSignInOptions
        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestProfile()
        .requestScopes(Scope(DriveScopes.DRIVE))
        .build()

      val googleSignInClient = GoogleSignIn.getClient(this, gso)
      val signInIntent = googleSignInClient.signInIntent
      startActivityForResult(signInIntent, CONST_SIGN_IN)
    } else {
      registerWork()
    }
  }

  private fun registerWork() {
    val downloadTranslationsRequest =
      PeriodicWorkRequestBuilder<DownloadTranslationsWork>(1, TimeUnit.HOURS).build()
    WorkManager.getInstance(this).enqueue(downloadTranslationsRequest)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == CONST_SIGN_IN && data != null) {
      handleSignData(data)
    }
  }

  private fun handleSignData(data: Intent) {
    GoogleSignIn.getSignedInAccountFromIntent(data)
      .addOnCompleteListener {
        if (it.isSuccessful){
          Log.d("DEBUGGG:", "Successful login")
          registerWork()
        } else {
          Log.d("DEBUGGG:", "Failed login")
        }
      }
  }

  companion object {
    const val CONST_SIGN_IN: Int = 1001
  }
}
