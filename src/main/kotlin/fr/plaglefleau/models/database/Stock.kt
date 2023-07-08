package fr.plaglefleau.models.database

data class Stock(val boutique: Boutique, val article: Article, val quantity: Int, val price: Double)
