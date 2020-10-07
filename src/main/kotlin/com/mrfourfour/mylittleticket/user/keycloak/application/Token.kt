package com.mrfourfour.mylittleticket.user.keycloak.application

import com.fasterxml.jackson.annotation.JsonProperty

data class Token(
        @JsonProperty("access_token")
        val accessToken: String,
        @JsonProperty("expires_in")
        val expiresIn: Long,
        @JsonProperty("refresh_expires_in")
        val refreshExpiresIn: Long,
        @JsonProperty("refresh_token")
        val refreshToken: String,
        @JsonProperty("token_type")
        val tokenType: String
)