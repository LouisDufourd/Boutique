package fr.plaglefleau.models.freemarker

import fr.plaglefleau.models.database.Boutique
import fr.plaglefleau.models.database.Rating

data class Shops(val boutique: Boutique, val rating: String, val userRating: Rating, val owner:String, val numberOfItem:Int, val numberOfMember:Int)
