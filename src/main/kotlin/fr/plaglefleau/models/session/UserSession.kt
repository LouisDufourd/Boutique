package fr.plaglefleau.models.session

import io.ktor.server.auth.*

data class UserSession(val userID : Int) : Principal