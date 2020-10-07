package com.mrfourfour.mylittleticket.user.keycloak.infrastructure

import com.mrfourfour.mylittleticket.user.keycloak.application.Token
import com.mrfourfour.mylittleticket.user.keycloak.application.TokenProvider
import com.mrfourfour.mylittleticket.user.keycloak.application.TokenRequest
import mu.KLogging
import org.apache.http.HttpHeaders
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

@Component
class KeycloakTokenProvider(
        @Qualifier("keycloakWebClient")
        private val webClient: WebClient,
        private val keycloakSpringBootProperties: KeycloakSpringBootProperties
) : TokenProvider {

    override fun issue(tokenRequest: TokenRequest): Token? {
        val (username, password) = tokenRequest
        val resource = keycloakSpringBootProperties.resource
        val clientSecret = keycloakSpringBootProperties.credentials["secret"] as String
        logger.debug ("resource: {}, clientSecret: {}", resource, clientSecret)
        val result = webClient.post()
                .uri { uriBuilder -> uriBuilder.pathSegment("token").build() }
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData("client_id", resource)
                                .with("grant_type", "password")
                                .with("client_secret", clientSecret)
                                .with("username", username)
                                .with("password", password))
                .retrieve()
                .bodyToMono(Token::class.java)
                .onErrorMap { throw IllegalArgumentException("message", it) }
                .block()
        logger.debug("result: {}", result)
        return result
    }

    override fun refresh(refreshToken: String) {
        TODO("Not yet implemented")
    }

    companion object: KLogging()
}

@Configuration
class KeyCloakWebClientConfig {

    @Bean
    fun keycloakWebClient(
            keycloakSpringBootProperties: KeycloakSpringBootProperties
    ): WebClient {
        val baseUrl = getBaseUrl(keycloakSpringBootProperties)
        logger.debug("baseUrl: {}", baseUrl)
        return WebClient
                .builder()
                .baseUrl(baseUrl)
                .build()
    }

    private fun getBaseUrl(keycloakSpringBootProperties: KeycloakSpringBootProperties) =
            "${keycloakSpringBootProperties.authServerUrl}/realms/" +
                    "${keycloakSpringBootProperties.realm}/protocol/openid-connect"

    companion object: KLogging()
}