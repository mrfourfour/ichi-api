package com.mrfourfour.mylittleticket.user.token.domain

class User private constructor(
        username: String
) {
    private val id : Long = 0

    var username = username
        private set

    companion object {
        fun of(username: String) =
                User(username)
    }
}