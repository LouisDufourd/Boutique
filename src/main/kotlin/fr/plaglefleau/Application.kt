package fr.plaglefleau

import fr.plaglefleau.bdd_MySql.Gestion
import fr.plaglefleau.plugins.configureRouting
import fr.plaglefleau.plugins.configureSecurity
import fr.plaglefleau.plugins.configureSerialization
import fr.plaglefleau.plugins.configureTemplating
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 80, host = "0.0.0.0", module = Application::module)
        .start(wait = false)

    val thread = Thread {
        while (true) {
            val gestion = Gestion()
            for (store in gestion.getStores()) {
                println("-${store.nom} :")
                for (member in gestion.getStoreMembers(store.id)) {
                    println("\t-${member.utilisateur.username} :")
                    try {
                        gestion.giveSalary(member)
                    } catch (e: Exception) {
                        System.err.println(e.message)
                    }
                }
            }
            //86400000 day in millisecond
            Thread.sleep(86400000L)
        }
    }
    thread.start()
}

fun Application.module() {
    configureSerialization()
    configureTemplating()
    configureSecurity()
    configureRouting()
}
