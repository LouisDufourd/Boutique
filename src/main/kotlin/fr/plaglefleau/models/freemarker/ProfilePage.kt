package fr.plaglefleau.models.freemarker

import fr.plaglefleau.models.database.Inventory
import fr.plaglefleau.models.database.Utilisateur

data class ProfilePage(
    val connectedpage: ConnectedPage,
    val user: Utilisateur,
    val editerror: Int,
    val inventory: Inventory?
)
