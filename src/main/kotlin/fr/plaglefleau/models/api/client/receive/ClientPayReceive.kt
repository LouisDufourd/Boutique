package fr.plaglefleau.models.api.client.receive

data class ClientPayReceive(val fromUser: String, val toUser : String, val amount: Double,val username: String, val password: String)
