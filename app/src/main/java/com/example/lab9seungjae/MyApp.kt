package com.example.lab9seungjae

import android.app.Application
import androidx.room.Room

class UserState(private val repository: UserRepository) {
    var users = repository.getAll().toMutableList()

    fun add(localUser: LocalUser) {
        repository.insertEntity(localUser)
    }

    fun refresh() {
        users.apply {
            clear()
            addAll(repository.getAll())
        }
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

    fun getAll(): List<LocalUser> {
        return userDao.getAll()
    }
}
