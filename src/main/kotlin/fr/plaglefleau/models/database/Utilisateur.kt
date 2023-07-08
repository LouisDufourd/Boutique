package fr.plaglefleau.models.database

import java.sql.Timestamp

data class Utilisateur(val id:Int, val username:String, val email : String, val password : String, val createTime : String, val nombreSlot : Int, val solde : Double)
