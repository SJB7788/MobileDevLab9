package com.example.lab9seungjae

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.room.Delete
import androidx.room.Room

class UserState(private val repository: UserRepository) {
    var users by mutableStateOf(repository.getAll())

    fun add(localUser: LocalUser) {
        repository.insertEntity(localUser)
    }

    @Delete
    fun delete(localUser: LocalUser) {
        repository.delete(localUser)
        refresh()
    }

    fun refresh() {
        users = repository.getAll()
    }
}
class MyApp : Application() {
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "my database")
            .allowMainThreadQueries()
            .build()
    }

    val userRepository by lazy {
        UserRepository(db.userDao())
    }

}

class UserRepository(private val userDao: UserDao) {
    fun insertEntity(user: LocalUser) {
        userDao.add(user)
    }

    fun delete(user: LocalUser) {
        userDao.delete(user)
    }


    fun getAll(): List<LocalUser> {
        return userDao.getAll()
    }
}
