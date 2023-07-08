package fr.plaglefleau.bdd_MySql

import java.sql.*

class Connexion(url:String, username:String, password:String) {

    private var conn: Connection


     init {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
        } catch (ex: ClassNotFoundException) {
            println("erreur com.mysql.jdbc.Driver")
            throw ex
        }
        try {
            conn = DriverManager.getConnection(url,username,password)
            voirLesBdd()
        } catch (ex: SQLException) {
            println("erreur de connexion à la bdd : \n${ex.message}")
            throw ex
        }
    }

    fun voirLesBdd():ArrayList<String>{
        val reponse = ArrayList<String>()
        lateinit var stmt: Statement
        lateinit var resultSet: ResultSet
        return try {
            stmt = conn.createStatement()
            resultSet = stmt.executeQuery("SHOW DATABASES")
            while (resultSet.next()) {
                reponse.add(resultSet.getString("Database"))
            }
            reponse
        } catch (ex: SQLException) {
            reponse.add("erreur SHOW DATABASES ")
            reponse
        }
    }

    fun fermerConnexion(){
        conn.close()
    }

    fun getConnexion():Connection{
        return conn
    }


}