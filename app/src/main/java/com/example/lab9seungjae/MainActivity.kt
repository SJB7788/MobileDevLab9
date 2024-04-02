package com.example.lab9seungjae

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import com.example.lab9seungjae.ui.theme.Lab9SeungJaeTheme
import kotlin.math.sign

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userRepository = (application as MyApp).userRepository

        setContent {
            val usersState = remember {
                UserState(userRepository)
            }
            Log.e("Testing", usersState.users.toString())
            MainContent(usersState)
        }
    }
}

@Composable
fun MainContent(usersState: UserState) {
    SignupComposable(usersState)
}

@Composable
fun MyTextField(title: String, value: String, onValueChanged:(String) -> Unit) {
    Text(text = title,
        modifier = Modifier
            .padding(5.dp))
    TextField(
       value = value,
        onValueChange = onValueChanged,
        textStyle = TextStyle(fontSize = 20.sp),
        modifier = Modifier
            .fillMaxWidth()
    )
}

class SignupState {
    var uid by mutableStateOf("")
    fun onUIDChanged(value: String) {
        uid = value
    }

    var name by mutableStateOf("")
    fun onNameChanged(value: String)  {
        name = value
    }

    var email by mutableStateOf("")
    fun onEmailChanged(value: String) {
        email = value
    }
}

@Composable
fun SignupComposable(userState: UserState) {
    val signupState = remember { SignupState() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        MyTextField(title = "UID", value = signupState.uid,
            onValueChanged = signupState::onUIDChanged)
        MyTextField(title = "Username", value = signupState.name,
            onValueChanged = signupState::onNameChanged)
        MyTextField(title = "Email", value = signupState.email,
            onValueChanged = signupState::onEmailChanged)

        Row ()
        {
            Button(onClick = {
                val uid = if (signupState.uid == "") { null } else { signupState.uid.toInt() }
                val user = LocalUser(uid, signupState.name, signupState.email)
                userState.add(user)
                Log.e("Testing", userState.users.toString())
        }) {
            Text(text = "Add")
        }
            Button(onClick = {
                userState.refresh()
                Log.e("Testing", userState.users.toString())
            }) {
                Text(text = "Refresh")
            }
        }

        LazyColumn {
            items(userState.users) { user ->
                UserCards(
                    name = user.userName.toString(),
                    email = user.email.toString(),
                    userState = userState,
                    user = user,
                    signupState = signupState
                )
            }
        }
    }
}

@Composable
fun UserCards(name: String, email: String, userState: UserState, user: LocalUser,
              signupState: SignupState) {
    Card (
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .clickable {
                signupState.uid = user.uid.toString()
                signupState.name = user.userName.toString()
                signupState.email = user.email.toString()
            }
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(modifier = Modifier.padding(vertical = 5.dp, horizontal = 20.dp), text = name)
            Text(modifier = Modifier.padding(vertical = 5.dp, horizontal = 20.dp), text = email)
            Button(modifier = Modifier.padding(vertical = 5.dp, horizontal = 20.dp),
                onClick = {
                    userState.delete(user)
                    Log.e("Testing", userState.users.toString())
                }
            ) {
                Text(text = "X")
            }
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(user: LocalUser)

    @Delete
    fun delete(user: LocalUser)
}

@Database(entities = [LocalUser::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao() : UserDao
}


