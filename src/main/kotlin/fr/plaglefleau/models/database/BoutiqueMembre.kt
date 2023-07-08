package fr.plaglefleau.models.database

import java.sql.Timestamp

data class BoutiqueMembre(val boutique: Boutique, val utilisateur: Utilisateur, val role: Role, val salary:String, val lastSalary:Timestamp, var missingrole: ArrayList<Role>?)