package fr.plaglefleau.plugins

import fr.plaglefleau.models.security.Bearer
import fr.plaglefleau.models.session.UserSession
import io.ktor.server.sessions.*
import io.ktor.server.response.*
import io.ktor.server.auth.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureSecurity() {

    install(Sessions) {
        cookie<UserSession>("connect") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 900
        }
    }

    authentication {
        session<UserSession>("user-session") {
            validate {session ->
                if(session.userID > 0) {
                    session
                } else {
                    null
                }
            }
            challenge {
                call.respondRedirect("/")
            }
        }
        bearer("api-bearer") {
            authenticate { tokenCredential ->
                if(tokenCredential.token == "abcd") {
                    UserIdPrincipal("api")
                } else {
                    UserIdPrincipal("api")
                }
            }
        }
    }
}
