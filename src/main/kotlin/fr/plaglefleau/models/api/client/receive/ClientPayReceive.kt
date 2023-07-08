package fr.plaglefleau.models.api.client.receive

data class ClientPayReceive(val fromUser: Int, val toUser : Int, val amount: Double,val username: String, val password: String)
