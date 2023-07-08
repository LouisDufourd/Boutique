package fr.plaglefleau.models.freemarker

import fr.plaglefleau.models.database.BoutiqueMembre

data class StoresPage(val connectedpage: ConnectedPage, val stores: ArrayList<BoutiqueMembre>, val roles: ArrayList<Int>)
