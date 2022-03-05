package com.dbtechprojects.simplenotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dbtechprojects.simplenotes.ui.theme.DragandDropDemoTheme
import com.dbtechprojects.simplenotes.ui.theme.Purple700

class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DragandDropDemoTheme {
                Surface(color = MaterialTheme.colors.background) {
                   AppScaffold()
                }
            }
        }
    }
}

@Composable
fun AppScaffold() {
    Scaffold(
        topBar = { TopAppBar(title = {Text(stringResource(id = R.string.app_name))},backgroundColor = Purple700)  },
        content = { Greeting(name = "Android") },
    )
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}
