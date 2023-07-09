package fr.plaglefleau.plugins

import com.google.gson.Gson
import fr.plaglefleau.bdd_MySql.Gestion
import fr.plaglefleau.models.api.boutique.receive.BoutiquePayReceive
import fr.plaglefleau.models.api.boutique.response.BoutiquePayResponse
import fr.plaglefleau.models.api.client.receive.*
import fr.plaglefleau.models.api.client.response.*
import fr.plaglefleau.models.database.Utilisateur
import fr.plaglefleau.models.freemarker.*
import fr.plaglefleau.models.session.UserSession
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.freemarker.*
import io.ktor.server.request.*
import io.ktor.server.sessions.*
import org.apache.commons.validator.routines.EmailValidator

val gestion = Gestion()
val invisibleCharRegex = Regex("[\\p{C}\\p{Z}]")

fun Application.configureRouting() {
    routing {
        get("/") {
            try {
                val homepage = getHomepageError(call)
                print(Gson().toJson(homepage))
                if (call.request.acceptLanguageItems()[0].value.startsWith("fr")) {
                    call.respond(
                        HttpStatusCode.OK,
                        FreeMarkerContent("fr/index.ftl", mapOf("data" to homepage))
                    )
                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        FreeMarkerContent("en/index.ftl", mapOf("data" to homepage))
                    )
                }
            } catch (e: Exception) {
                giveErrorPage(call, e)
            }
        }
        staticResources("/assets", "assets")
        route("/") {
            get("connect") {
                try {
                    val username = call.parameters["username"]!!
                    val password = call.parameters["password"]!!
                    val userConnect = gestion.connexion(username, password)
                    if (userConnect.isGoodLogin) {
                        call.sessions.set("connect", UserSession(userConnect.id))
                        call.respondRedirect("/")
                    } else {
                        call.respondRedirect("/?connexionError=true")
                    }
                } catch (e: Exception) {
                    giveErrorPage(call, e)
                }
            }
            get("register") {
                try {
                    val user = call.parameters["username"]!!
                    val pass = call.parameters["password"]!!
                    val email = call.parameters["email"]!!
                    if (invisibleCharRegex.findAll(user).map { it.value }.toList()
                            .isNotEmpty() || user == "null" || user == ""
                    ) {
                        call.respondRedirect("/?registerError=2")
                    } else if (invisibleCharRegex.findAll(pass).map { it.value }.toList()
                            .isNotEmpty() || pass == "null" || pass == ""
                    ) {
                        call.respondRedirect("/?registerError=3")
                    } else {
                        val result = gestion.inscription(user, pass, email)
                        call.respondRedirect("/?registerError=${result}")
                    }
                } catch (e: Exception) {
                    giveErrorPage(call, e)
                }
            }
            authenticate("user-session") {
                get("profile") {
                    try {
                        var createShop = call.parameters["createShopError"].toString().toIntOrNull()
                        var editError = call.parameters["editError"].toString().toIntOrNull()
                        if (editError == null) {
                            editError = 0
                        }
                        if (createShop == null) {
                            createShop = 0
                        }
                        val utilisateur = gestion.getUtilisateur(call.sessions.get<UserSession>()!!.userID)
                        val profilePage = ProfilePage(ConnectedPage(createShop), utilisateur!!, editError)
                        if (isFrench(call)) {
                            call.respond(
                                HttpStatusCode.OK,
                                FreeMarkerContent("fr/profile.ftl", mapOf("data" to profilePage))
                            )
                        } else {
                            call.respond(
                                HttpStatusCode.OK,
                                FreeMarkerContent("en/profile.ftl", mapOf("data" to profilePage))
                            )
                        }
                    } catch (e: Exception) {
                        giveErrorPage(call, e)
                    }
                }
                route("stores") {
                    get("") {
                        storesList(call)
                    }
                    get("/") {
                        storesList(call)
                    }
                    get("/{id}") {
                        try {
                            val id = call.parameters["id"].toString().toIntOrNull()
                            var addMemberError = call.parameters["addMemberError"].toString().toIntOrNull()
                            var createShop = call.parameters["createShopError"].toString().toIntOrNull()
                            if (addMemberError == null) {
                                addMemberError = 0
                            }
                            if (createShop == null) {
                                createShop = 0
                            }
                            if (id == null) {
                                call.respondRedirect("/stores")
                            } else {
                                val roleID = gestion.getUserRoleID(id, call.sessions.get<UserSession>()!!.userID)
                                if (roleID == null) {
                                    call.respondRedirect("/stores")
                                } else {
                                    var members = gestion.getStoreMembers(id)
                                    val users = gestion.getAllUser(id)
                                    val stocks = gestion.getStock(id)
                                    members = gestion.getAllRoles(members, roleID)
                                    println(Gson().toJson(users))
                                    if (isFrench(call)) {
                                        call.respond(
                                            HttpStatusCode.OK,
                                            FreeMarkerContent(
                                                "fr/manageStore.ftl",
                                                mapOf(
                                                    "data" to ManageStorePage(
                                                        members,
                                                        Gson().toJson(users),
                                                        id,
                                                        roleID,
                                                        addMemberError,
                                                        stocks,
                                                        ConnectedPage(
                                                            createShop!!
                                                        )
                                                    )
                                                )
                                            )
                                        )
                                    } else {
                                        call.respond(
                                            HttpStatusCode.OK,
                                            FreeMarkerContent(
                                                "en/manageStore.ftl",
                                                mapOf(
                                                    "data" to ManageStorePage(
                                                        members,
                                                        Gson().toJson(users),
                                                        id,
                                                        roleID,
                                                        addMemberError,
                                                        stocks,
                                                        ConnectedPage(
                                                            createShop!!
                                                        )
                                                    )
                                                )
                                            )
                                        )
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            giveErrorPage(call, e)
                        }
                    }
                    get("/{id}/delete") {
                        val id = call.parameters["id"].toString().toIntOrNull()
                        if (id == null) {
                            call.respondRedirect("/stores")
                        } else {
                            gestion.deleteStand(id)
                            call.respondRedirect("/stores")
                        }
                    }
                }

                get("editUser") {
                    try {
                        val id = call.sessions.get<UserSession>()!!.userID
                        val username = call.parameters["username"].toString()
                        val password = call.parameters["password"].toString()
                        val email = call.parameters["email"]
                        if (invisibleCharRegex.findAll(username).map { it.value }.toList()
                                .isNotEmpty() || username == "null"
                        ) {
                            call.respondRedirect("/profile?editError=2")
                        } else if (invisibleCharRegex.findAll(password).map { it.value }.toList()
                                .isNotEmpty() || password == "null"
                        ) {
                            call.respondRedirect("/profile?editError=3")
                        } else {
                            call.respondRedirect(
                                "/profile?editError=${
                                    gestion.editUser(
                                        id,
                                        username,
                                        password,
                                        email!!
                                    )
                                }"
                            )
                        }
                    } catch (e: Exception) {
                        giveErrorPage(call, e)
                    }
                }
                get("editSalary") {
                    try {
                        val url = call.parameters["url"].toString()
                        val salary = call.parameters["salary"].toString().toDoubleOrNull()
                        val boutiqueId = call.parameters["boutiqueID"].toString().toIntOrNull()
                        val userId = call.parameters["userID"].toString().toIntOrNull()
                        if (url == "null" || salary == null || boutiqueId == null || userId == null) {
                            call.respondRedirect("/")
                        } else {
                            gestion.editSalary(salary, boutiqueId, userId)
                            call.respondRedirect(url)
                        }
                    } catch (e: Exception) {
                        giveErrorPage(call, e)
                    }
                }
                get("editMember") {
                    val url = call.parameters["url"].toString()
                    val userID = call.parameters["userID"].toString().toIntOrNull()
                    val boutiqueID = call.parameters["boutiqueID"].toString().toIntOrNull()
                    val roleID = call.parameters["roleID"].toString().toIntOrNull()
                    if (url == "null" || userID == null || boutiqueID == null || roleID == null) {
                        call.respondRedirect("/")
                    } else {
                        if (gestion.isTheUserHaveTheRights(call.sessions.get<UserSession>()!!.userID, boutiqueID, 2)) {
                            gestion.editMember(userID, boutiqueID, roleID)
                            call.respondRedirect(url)
                        } else {
                            call.respondRedirect("/")
                        }
                    }
                }
                get("editStock") {
                    handleStockRequest(call)
                }

                get("bailoutStock") {
                    handleStockRequest(call)
                }
                get("addStock") {
                    handleStockRequest(call)
                }
                get("ajouterUnMembre") {
                    val storeID = call.parameters["store"].toString().toIntOrNull()
                    val username = call.parameters["username"].toString()
                    val url = call.parameters["url"].toString()
                    if (storeID == null || username == "null") {
                        if (url == "null") {
                            call.respondRedirect("/")
                        } else {
                            call.respondRedirect(url)
                        }
                    } else {

                        if (gestion.isTheUserHaveTheRights(call.sessions.get<UserSession>()!!.userID, storeID, 2)) {
                            call.respondRedirect("$url?addMemberError=${gestion.addMember(storeID, username)}")
                        }
                    }
                }
                get("createShop") {
                    try {
                        val shopName = call.parameters["shopName"].toString()
                        var previousUrl = call.parameters["url"]
                        if (previousUrl == null) {
                            previousUrl = ""
                        }
                        val userID = call.sessions.get<UserSession>()!!.userID
                        if (invisibleCharRegex.findAll(shopName.replace(" ", "")).map { it.value }.toList()
                                .isNotEmpty() || shopName == "null" || shopName == ""
                        ) {
                            call.respondRedirect("${previousUrl}?createShopError=2")
                        } else {
                            call.respondRedirect(
                                "${previousUrl}?createShopError=${
                                    gestion.createShop(
                                        shopName,
                                        userID
                                    )
                                }"
                            )
                        }
                    } catch (e: Exception) {
                        giveErrorPage(call, e)
                    }
                }

                get("removeMember") {
                    val url = call.parameters["url"].toString()
                    val userID = call.parameters["userID"].toString().toIntOrNull()
                    val boutiqueID = call.parameters["boutiqueID"].toString().toIntOrNull()
                    if (url == "null" || userID == null || boutiqueID == null) {
                        call.respondRedirect("/")
                    } else {
                        if (gestion.isTheUserHaveTheRights(call.sessions.get<UserSession>()!!.userID, boutiqueID, 2)) {
                            gestion.removeMember(userID, boutiqueID)
                            call.respondRedirect(url)
                        } else {
                            call.respondRedirect("/")
                        }
                    }
                }

                get("disconnect") {
                    try {
                        call.sessions.clear("connect")
                        call.respondRedirect("/")
                    } catch (e: Exception) {
                        giveErrorPage(call, e)
                    }
                }
            }
        }
        authenticate("api-bearer") {
            route("/api/") {
                route("client/") {
                    get("connect/{username}/{password}") {
                        val username = call.parameters["username"].toString()
                        val password = call.parameters["password"].toString()
                        val userConnect = gestion.connexion(username, password)
                        if (userConnect.isGoodLogin) {
                            val user = gestion.getUtilisateur(
                                userConnect.id
                            )!!
                            call.respond(
                                HttpStatusCode.OK,
                                ClientConnectResponse(
                                    user,
                                    true,
                                    "Welcome ${user.username}"
                                )
                            )
                        } else {
                            call.respond(
                                HttpStatusCode.NotFound,
                                ClientConnectResponse(
                                    Utilisateur(
                                        -1,
                                        "",
                                        "",
                                        "",
                                        "",
                                        0,
                                        0.0
                                    ),
                                    false,
                                    "Incorrect username or password"
                                )
                            )
                        }
                    }
                    get("inventory/{username}") {
                        TODO("Not Implemented Yet")
                        val slots = ArrayList<Int>()
                        for (i in 0..26) {
                            slots.add(i)
                        }
                        val inv = "rO0ABXcEAAAAKXNyABpvcmcuYnVra2l0LnV0aWwuaW8uV3JhcHBlcvJQR+zxEm8FAgABTAADbWFwdAAPTGphdmEvdXRpbC9NYXA7eHBzcgA1Y29tLmdvb2dsZS5jb21tb24uY29sbGVjdC5JbW11dGFibGVNYXAkU2VyaWFsaXplZEZvcm0AAAAAAAAAAAIAAlsABGtleXN0ABNbTGphdmEvbGFuZy9PYmplY3Q7WwAGdmFsdWVzcQB+AAR4cHVyABNbTGphdmEubGFuZy5PYmplY3Q7kM5YnxBzKWwCAAB4cAAAAAR0AAI9PXQAAXZ0AAR0eXBldAAEbWV0YXVxAH4ABgAAAAR0AB5vcmcuYnVra2l0LmludmVudG9yeS5JdGVtU3RhY2tzcgARamF2YS5sYW5nLkludGVnZXIS4qCk94GHOAIAAUkABXZhbHVleHIAEGphdmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAKGnQAC0NHTV9TSE9UR1VOc3EAfgAAc3EAfgADdXEAfgAGAAAAA3EAfgAIdAAJbWV0YS10eXBldAAIaW50ZXJuYWx1cQB+AAYAAAADdAAISXRlbU1ldGF0AApVTlNQRUNJRklDdAC0SDRzSUFBQUFBQUFBQUgzTVFRcURNQlNFNGJGQnNIRlI4RGpTamRnYmVBQ1JHQkxSK0VyeWluZnlsUHB3NDZyYkdiNWZBd3JQSmdScTZiY3lnRUloYjJtaGlQZG4xeWdiNXNINFlGZE9HbmxuNkdzTFBLWVJMK05DblR4RjdwT3NtYml6a1drb0h0dzlBd2hsTXZORks2R2JuWnhuTy9aSmpyOGFPQUMvdVJWOXFBQUFBQT09c3EAfgAAc3EAfgADdXEAfgAGAAAABHEAfgAIcQB+AAlxAH4ACnEAfgALdXEAfgAGAAAABHEAfgANc3EAfgAOAAAKGnQACkNHTV9QSVNUT0xzcQB+AABzcQB+AAN1cQB+AAYAAAADcQB+AAhxAH4AFXEAfgAWdXEAfgAGAAAAA3EAfgAYcQB+ABl0AJRINHNJQUFBQUFBQUFBRTNNUVE1QU1CQkc0WjlHUW0yY1IyeWF1b0VEaUZTRHhIUkV4NldjVXJ1emZjbjdOS0RRR0NLMi9BUUIwQ2xVbGsrK01ZeXZSbXRFRnJlVER4STFxc254NVd1VXg0ck9iZFNUWDQrSDVwaHprY2VFRkJwS2x1M3ZKQmNmTFZSakxHc0FBQUE9c3EAfgAAc3EAfgADdXEAfgAGAAAABHEAfgAIcQB+AAlxAH4ACnQABmFtb3VudHVxAH4ABgAAAARxAH4ADXNxAH4ADgAAChp0AAlDR01fU0hFTExzcQB+AA4AAABAc3EAfgAAc3EAfgADdXEAfgAGAAAABHEAfgAIcQB+AAlxAH4ACnEAfgApdXEAfgAGAAAABHEAfgANc3EAfgAOAAAKGnEAfgAscQB+AC1zcQB+AABzcQB+AAN1cQB+AAYAAAAEcQB+AAhxAH4ACXEAfgAKcQB+ACl1cQB+AAYAAAAEcQB+AA1zcQB+AA4AAAoadAAQQ0dNX0JBU0lDX0JVTExFVHEAfgAtc3EAfgAAc3EAfgADdXEAfgAGAAAABHEAfgAIcQB+AAlxAH4ACnEAfgApdXEAfgAGAAAABHEAfgANc3EAfgAOAAAKGnEAfgA4c3EAfgAOAAAAEHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHNxAH4AAHNxAH4AA3VxAH4ABgAAAARxAH4ACHEAfgAJcQB+AApxAH4AC3VxAH4ABgAAAARxAH4ADXNxAH4ADgAAChp0AA1MRUFUSEVSX0JPT1RTc3EAfgAAc3EAfgADdXEAfgAGAAAABHEAfgAIcQB+ABV0AAtVbmJyZWFrYWJsZXQABWNvbG9ydXEAfgAGAAAABHEAfgAYdAANTEVBVEhFUl9BUk1PUnNyABFqYXZhLmxhbmcuQm9vbGVhbs0gcoDVnPruAgABWgAFdmFsdWV4cAFzcQB+AABzcQB+AAN1cQB+AAYAAAAEcQB+AAh0AANSRUR0AARCTFVFdAAFR1JFRU51cQB+AAYAAAAEdAAFQ29sb3JzcQB+AA4AAAAAc3EAfgAOAAAA/3EAfgBWc3EAfgAAc3EAfgADdXEAfgAGAAAABHEAfgAIcQB+AAlxAH4ACnEAfgALdXEAfgAGAAAABHEAfgANc3EAfgAOAAAKGnQAEExFQVRIRVJfTEVHR0lOR1NzcQB+AABzcQB+AAN1cQB+AAYAAAAEcQB+AAhxAH4AFXEAfgBIcQB+AEl1cQB+AAYAAAAEcQB+ABhxAH4AS3EAfgBNc3EAfgAAc3EAfgADdXEAfgAGAAAABHEAfgAIcQB+AFFxAH4AUnEAfgBTdXEAfgAGAAAABHEAfgBVcQB+AFZzcQB+AA4AAAD/cQB+AFZzcQB+AABzcQB+AAN1cQB+AAYAAAAEcQB+AAhxAH4ACXEAfgAKcQB+AAt1cQB+AAYAAAAEcQB+AA1zcQB+AA4AAAoadAASTEVBVEhFUl9DSEVTVFBMQVRFc3EAfgAAc3EAfgADdXEAfgAGAAAABHEAfgAIcQB+ABVxAH4ASHEAfgBJdXEAfgAGAAAABHEAfgAYcQB+AEtxAH4ATXNxAH4AAHNxAH4AA3VxAH4ABgAAAARxAH4ACHEAfgBRcQB+AFJxAH4AU3VxAH4ABgAAAARxAH4AVXEAfgBWc3EAfgAOAAAA/3EAfgBWc3EAfgAAc3EAfgADdXEAfgAGAAAABHEAfgAIcQB+AAlxAH4ACnEAfgALdXEAfgAGAAAABHEAfgANc3EAfgAOAAAKGnQADkxFQVRIRVJfSEVMTUVUc3EAfgAAc3EAfgADdXEAfgAGAAAABHEAfgAIcQB+ABVxAH4ASHEAfgBJdXEAfgAGAAAABHEAfgAYcQB+AEtxAH4ATXNxAH4AAHNxAH4AA3VxAH4ABgAAAARxAH4ACHEAfgBRcQB+AFJxAH4AU3VxAH4ABgAAAARxAH4AVXEAfgBWc3EAfgAOAAAA/3EAfgBWcA=="
                        /*call.respond(
                            InventoryReceive(
                                Inventory(
                                    inv,
                                    slots
                                ),
                                "plag"
                            )
                        )*/
                    }

                    post("register") {
                        val registerReceive = call.receive<RegisterReceive>()
                        if (invisibleCharRegex.matches(registerReceive.username)
                            || invisibleCharRegex.matches(registerReceive.password)
                        ) {
                            call.respond(
                                HttpStatusCode.NoContent,
                                ClientRegisterResponse(
                                    "The username or password is invalid"
                                )
                            )
                        } else {
                            if (!verifyEmailAddress(registerReceive.email)) {
                                call.respond(
                                    ClientRegisterResponse(
                                        "The email is incorrect"
                                    )
                                )
                            } else {
                                if (registerReceive.username.length < 4 || registerReceive.password.length < 8) {
                                    call.respond(
                                        HttpStatusCode.LengthRequired,
                                        ClientRegisterResponse(
                                            "The username need 4 character minimum and the password need 8 character minimum"
                                        )
                                    )
                                } else {
                                    when (gestion.inscription(
                                        registerReceive.username,
                                        registerReceive.password,
                                        registerReceive.email
                                    )) {
                                        0 -> {
                                            call.respond(
                                                HttpStatusCode.OK,
                                                ClientRegisterResponse(
                                                    "User has been created"
                                                )
                                            )
                                        }

                                        1 -> {
                                            call.respond(
                                                HttpStatusCode.OK,
                                                ClientRegisterResponse(
                                                    "The username is already in use"
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    put("debit") {
                        try {
                            val debitReceive = call.receive<DebitReceive>()
                            if (debitReceive.amount < 0.0) {
                                call.respond(
                                    ClientDebitResponse(
                                        "The amount can't be negative"
                                    )
                                )
                            } else {
                                when (gestion.debit(debitReceive.id, debitReceive.amount)) {
                                    1 -> {
                                        call.respond(
                                            ClientDebitResponse(
                                                "The account have been edited"
                                            )
                                        )
                                    }

                                    2 -> {
                                        call.respond(
                                            ClientDebitResponse(
                                                "You don't have enough money"
                                            )
                                        )
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            call.respond(
                                ClientDebitResponse(
                                    e.message!!
                                )
                            )
                        }
                    }
                    put("credit") {
                        try {
                            val creditReceive = call.receive<CreditReceive>()
                            if (creditReceive.amount < 0.0) {
                                call.respond(
                                    ClientCreditResponse(
                                        "The amount can't be negative"
                                    )
                                )
                            } else {
                                when (gestion.credit(creditReceive.id, creditReceive.amount)) {
                                    1 -> {
                                        call.respond(
                                            ClientCreditResponse(
                                                "The account has been edited"
                                            )
                                        )
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            call.respond(
                                ClientCreditResponse(
                                    e.message!!
                                )
                            )
                        }
                    }
                    put("pay") {
                        try {
                            val clientPayReceive = call.receive<ClientPayReceive>()
                            if (clientPayReceive.amount < 0.0) {
                                call.respond(
                                    ClientPayResponse(
                                        "The amount can't be negative"
                                    )
                                )
                            } else {
                                if (gestion.connexion(
                                        clientPayReceive.username,
                                        clientPayReceive.password
                                    ).id != clientPayReceive.fromUser
                                ) {
                                    call.respond(
                                        ClientPayResponse(
                                            "You don't have the right to do that"
                                        )
                                    )
                                } else {
                                    when (gestion.pay(
                                        clientPayReceive.fromUser,
                                        clientPayReceive.toUser,
                                        clientPayReceive.amount
                                    )) {
                                        1 -> {
                                            call.respond(
                                                HttpStatusCode.OK,
                                                ClientPayResponse(
                                                    "The user has been payed"
                                                )
                                            )
                                        }

                                        2 -> {
                                            call.respond(
                                                ClientPayResponse(
                                                    "The user ${clientPayReceive.fromUser} don't exist"
                                                )
                                            )
                                        }

                                        3 -> {
                                            call.respond(
                                                ClientPayResponse(
                                                    "The user ${clientPayReceive.fromUser} don't have enough money"
                                                )
                                            )
                                        }

                                        4 -> {
                                            call.respond(
                                                ClientPayResponse(
                                                    "The user ${clientPayReceive.toUser} don't exist"
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            call.respond(
                                ClientPayResponse(
                                    e.message!!
                                )
                            )
                        }
                    }
                    put("updateInventory") {
                        val receive = call.receive<InventoryReceive>()
                        gestion.updateInventory(receive.inventory, receive.username)
                        call.respond(HttpStatusCode.OK, "Good")
                    }
                }
                route("store/") {
                    put("pay") {
                        try {
                            val boutiquePayReceive = call.receive<BoutiquePayReceive>()
                            if (boutiquePayReceive.amount < 0.0) {
                                call.respond(
                                    BoutiquePayResponse(
                                        "The amount can't be negative"
                                    )
                                )
                            } else {
                                when (
                                    gestion.payBoutique(
                                        boutiquePayReceive.user,
                                        boutiquePayReceive.boutique,
                                        boutiquePayReceive.amount,
                                        boutiquePayReceive.username,
                                        boutiquePayReceive.password
                                    )
                                ) {
                                    1 -> {
                                        call.respond(
                                            BoutiquePayResponse(
                                                "The user ${boutiquePayReceive.user} has been paid"
                                            )
                                        )
                                    }

                                    2 -> {
                                        call.respond(
                                            BoutiquePayResponse(
                                                "The store ${boutiquePayReceive.boutique} don't exist"
                                            )
                                        )
                                    }

                                    3 -> {
                                        call.respond(
                                            BoutiquePayResponse(
                                                "The user ${boutiquePayReceive.user} don't exist"
                                            )
                                        )
                                    }

                                    4 -> {
                                        call.respond(
                                            BoutiquePayResponse(
                                                "The store ${boutiquePayReceive.boutique} don't have enough money do pay this the user ${boutiquePayReceive.user}"
                                            )
                                        )
                                    }

                                    5 -> {
                                        call.respond(
                                            BoutiquePayResponse(
                                                "The user ${boutiquePayReceive.username} don't have the right to do that"
                                            )
                                        )
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            call.respond(
                                BoutiquePayResponse(
                                    e.message!!
                                )
                            )
                        }
                    }
                    put("buy") {
                    }
                }
            }
        }
    }

}


suspend fun handleStockRequest(call: ApplicationCall) {
    val url = call.parameters["url"].toString()
    val articleID = call.parameters["articleID"]?.toIntOrNull()
    val boutiqueID = call.parameters["boutiqueID"]?.toIntOrNull()

    if (url == "null" || articleID == null || boutiqueID == null) {
        call.respondRedirect("/")
        return
    }

    val userSession = call.sessions.get<UserSession>()
    if (userSession == null) {
        call.respondRedirect("/")
        return
    }

    when (call.request.path()) {
        "/editStock" -> {
            val price = call.parameters["price"]?.toDoubleOrNull()
            if (price != null && gestion.isTheUserHaveTheRights(userSession.userID, boutiqueID, 2)) {
                gestion.editStock(articleID, boutiqueID, price)
            }
        }
        "/addStock" -> {
            val quantity = call.parameters["quantity"]?.toIntOrNull()
            val price = call.parameters["price"]?.toDoubleOrNull()
            if (quantity != null && price != null && gestion.isTheUserHaveTheRights(userSession.userID, boutiqueID, 2)) {
                gestion.addStock(articleID, boutiqueID, quantity, price)
            }
        }
        "/bailoutStock" -> {
            val quantity = call.parameters["quantity"]?.toIntOrNull()
            if (quantity != null && quantity >= 0) {
                if (gestion.removeItemFromInventory(userSession.userID, articleID, quantity) &&
                    gestion.isTheUserHaveTheRights(userSession.userID, boutiqueID, 3)
                ) {
                    gestion.bailoutStock(articleID, boutiqueID, quantity)
                }
            }
        }
    }

    call.respondRedirect(url)
}

suspend fun storesList(call: ApplicationCall) {
    try {
        var createShop = call.parameters["createShopError"].toString().toIntOrNull()
        if (createShop == null) {
            createShop = 0
        }
        val stores = gestion.getStores(call.sessions.get<UserSession>()!!.userID)
        val storesPage = StoresPage(
            ConnectedPage(
                createShop
            ),
            stores,
            gestion.getUserRolesID(stores, call.sessions.get<UserSession>()!!.userID)
        )
        if (isFrench(call)) {
            call.respond(
                HttpStatusCode.OK,
                FreeMarkerContent(
                    "/fr/stores.ftl",
                    mapOf("data" to storesPage)
                )
            )
        } else {
            call.respond(
                HttpStatusCode.OK,
                FreeMarkerContent(
                    "/en/stores.ftl",
                    mapOf("data" to storesPage)
                )
            )
        }
    } catch (e: Exception) {
        giveErrorPage(call, e)
    }
}

suspend fun giveErrorPage(call: ApplicationCall, e: Exception) {
    System.err.println(e.message)
    call.respond(HttpStatusCode.InternalServerError, e)
}

fun getHomepageError(call: ApplicationCall): Homepage {
    var connexionError = call.parameters["connexionError"].toString().toBooleanStrictOrNull()
    var registerError = call.parameters["registerError"].toString().toIntOrNull()
    var createShopError = call.parameters["createShopError"].toString().toIntOrNull()
    val isConnected = call.sessions.get<UserSession>() != null
    if (connexionError == null) {
        connexionError = false
    }
    if (registerError == null) {
        registerError = 0
    }
    if (createShopError == null) {
        createShopError = 0
    }
    return if (isConnected) {
        Homepage(connected = true, connexionerror = false, registererror = 0, createshoperror = createShopError)
    } else {
        Homepage(false, connexionError, registerError, 0)
    }
}

fun isFrench(call: ApplicationCall): Boolean {
    return call.request.acceptLanguageItems()[0].value.startsWith("fr")
}

fun verifyEmailAddress(email: String): Boolean {
    val validator = EmailValidator.getInstance()
    return validator.isValid(email)
}