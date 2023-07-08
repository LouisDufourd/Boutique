package fr.plaglefleau.models.security

import io.ktor.server.auth.*

data class UserIdPrincipal(val user: String) : Principal