package test.sls1005.projects.editorskeyboard

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import test.sls1005.projects.editorskeyboard.ui.theme.EditorsKeyboardTheme

class DocumentationActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EditorsKeyboardTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text("Documentation", fontWeight = FontWeight.Bold)
                            }
                        )
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding).verticalScroll(rememberScrollState())
                    ) {
                        Text("Keyboard Types", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp))
                        Text("This app provides similar but slightly different types of keyboards:\n\n    Auxiliary: cannot be set as the default input method; sometimes not shown in the keyboard-switching menu.\n\n    Non-auxiliary: \"normal\", not auxiliary keyboard; can be set as a default input method.\n\n    Plain-text mode: will only copy/paste as plain (non-rich) texts.\n\n    Keycode-first: will send keycodes by default. (i.e., will simulate a Ctrl-C when you short click the \"COPY\" button, even if Ctrl-C might not mean \"copy\" in the app you would be using.)\n\nEach can be enabled/disabled independently, but none of them can be used as a normal input method; they can only be used for editing, not inputting. The keyboard layouts are all same; only behaviors differ. You typically don't need all of them.\n\nLanguages other than English are not fully supported yet, and the buttons might not be localized due to aesthetic or usability-related reasons.\n\nLong pressing the backspace key will always cause this keyboard to simulate a Ctrl-Z. It is not a bug.\n", fontSize = 20.sp, modifier = Modifier.padding(20.dp))
                        // The design is intended.
                        Text("About", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp))
                        Text(
                            buildAnnotatedString {
                                val url = "https://github.com/sls1005/EditorKeyboard"
                                append("The source code of this app is available at ")
                                withLink(
                                    LinkAnnotation.Url(url, styles = TextLinkStyles(SpanStyle(color = Color(0xFF3792FA)))),
                                ) { append(url) }
                                append(", under the MIT License")
                            },
                            fontSize = 20.sp,
                            lineHeight = 26.sp,
                            modifier = Modifier.padding(20.dp)
                        )
                        TextButton(
                            onClick = {
                                startActivity(
                                    Intent(this@DocumentationActivity, ShowOpenSourceLibrariesActivity::class.java)
                                )
                            }, modifier = Modifier.fillMaxWidth().padding(10.dp)
                        ) {
                            Text("See open source libraries used", fontSize = 20.sp)
                        }
                    }
                }
            }
        }
    }
}