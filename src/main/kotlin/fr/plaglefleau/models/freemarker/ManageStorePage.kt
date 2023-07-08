package fr.plaglefleau.models.freemarker

import fr.plaglefleau.models.database.BoutiqueMembre
import fr.plaglefleau.models.database.Role
import fr.plaglefleau.models.database.Stock

data class ManageStorePage(
    val members: ArrayList<BoutiqueMembre>,
    val users: String,
    val storeid: Int,
    val roleid: Int,
    val addmembererror: Int,
    val stocks: ArrayList<Stock>,
    val connectedpage: ConnectedPage,
)
