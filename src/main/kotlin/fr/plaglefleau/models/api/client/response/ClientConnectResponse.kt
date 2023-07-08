package fr.plaglefleau.models.api.client.response

import fr.plaglefleau.models.database.Utilisateur

data class ClientConnectResponse(val client: Utilisateur, val isGoodLogin: Boolean, val message: String)
