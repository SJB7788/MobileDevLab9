package com.example.lab9seungjae

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import com.example.lab9seungjae.ui.theme.Lab9SeungJaeTheme
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userRepository = (application as MyApp).userRepository

        setContent {
            val usersState = remember {
                UserState(userRepository)
            }
            MainContent(usersState)
        }
    }
}

@Composable
fun MainContent(usersState: UserState) {
    // make ui now
}

@Composable
fun MyTextField(value: String, onValueChanged:(String) -> Unit) {
    TextField(
       value = value,
        onValueChange = onValueChanged,
        textStyle = TextStyle(fontSize = 20.sp)
    )
}

class SignupState {

    var name by mutableStateOf("")
    fun onNameChanged(value: String)  {
        name = value
    }

    var email by mutableStateOf("")
    var validEmail = false
    fun onEmailChanged(value: String) {
        email = value
        validEmail = email.contains("@")
    }
}

@Composable
fun MySignupComposable() {
    val signupState = remember { SignupState() }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        MyTextField(value = signupState.name, onValueChanged = signupState::onNameChanged)
        MyTextField(value = signupState.email, onValueChanged = signupState::onEmailChanged)
        if (!signupState.validEmail) {
            Text(
                text = "Invalid Email",
                color = Color.Red
            )
        }
    }
}

@Entity(tableName = "user_table")
data class LocalUser (
    @PrimaryKey(autoGenerate = true) val uid: Int? = null,
    @ColumnInfo(name = "user_name") val userName: String?,
    val email: String?
)

@Dao
interface UserDao {
    @Query("SELECT * FROM user_table")
    fun getAll(): List<LocalUser>

    @Insert
    fun add(user: LocalUser)
}

@Database(entities = [LocalUser::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao() : UserDao
}


