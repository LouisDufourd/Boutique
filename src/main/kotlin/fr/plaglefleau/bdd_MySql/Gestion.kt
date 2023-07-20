package fr.plaglefleau.bdd_MySql

import com.google.gson.Gson
import fr.plaglefleau.models.database.*
import fr.plaglefleau.models.minecraft.Item
import fr.plaglefleau.models.minecraft.ItemSlotPair
import fr.plaglefleau.models.session.UserConnect
import java.sql.ResultSet
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class Gestion {
    private val url = "jdbc:mysql://127.0.0.1:3306/boutique"
    private val username = "Plag"
    private val password = "PiWizupAl12u"

    fun connexion(identifiant: String, password: String): UserConnect {
        val laConnexion = Connexion(url, username, this.password)
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT id FROM user WHERE (username = ? OR UUID = ?) AND password = MD5(?)"
        )
        preparedStatement.setString(1, identifiant)
        preparedStatement.setString(2, identifiant)
        preparedStatement.setString(3, password)
        val rs = preparedStatement.executeQuery()
        val userConnect = if (rs.next()) {
            UserConnect(rs.getInt("id"), true)
        } else {
            UserConnect(-1, false)
        }
        laConnexion.fermerConnexion()
        return userConnect
    }

    fun getUtilisateur(userID: Int): Utilisateur? {
        val laConnexion = Connexion(url, username, password)
        val preparedStatement = laConnexion.getConnexion().prepareStatement("SELECT * FROM user WHERE id = ?")
        preparedStatement.setInt(1, userID)
        val rs = preparedStatement.executeQuery()
        val utilisateur = if (rs.next()) {
            Utilisateur(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("email"),
                "",
                rs.getTimestamp("create_time").toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")),
                rs.getInt("nombre_slot"),
                rs.getDouble("solde")
            )
        } else {
            return null
        }
        laConnexion.fermerConnexion()
        return utilisateur
    }

    fun editUser(id: Int, username: String, password: String, email: String): Int {
        val laConnexion = Connexion(url, this.username, this.password)
        var preparedStatement =
            laConnexion.getConnexion().prepareStatement("SELECT COUNT(*) FROM user WHERE username = ? AND id != ?")
        preparedStatement.setString(1, username)
        preparedStatement.setInt(2, id)
        val rs = preparedStatement.executeQuery()
        if (rs.next()) {
            if (rs.getInt(1) >= 1) {
                laConnexion.fermerConnexion()
                return 1
            }
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE user " +
                    "SET username = ?, password = MD5(?), email = ? " +
                    "WHERE id = ?"
        )
        preparedStatement.setString(1, username)
        preparedStatement.setString(2, password)
        preparedStatement.setString(3, email)
        preparedStatement.setInt(4, id)
        preparedStatement.executeUpdate()
        laConnexion.fermerConnexion()
        return 0
    }

    fun inscription(user: String, UUID:String, pass: String, email: String): Int {
        val laConnexion = Connexion(url, username, password)
        var preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT NULL FROM user WHERE username = ? OR UUID = ?"
        )
        preparedStatement.setString(1, user)
        preparedStatement.setString(2, UUID)
        var rs = preparedStatement.executeQuery()
        if (rs.next()) {
            laConnexion.fermerConnexion()
            return 1
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "INSERT INTO user (id,username,UUID,password,email,create_time,nombre_slot) " +
                    "VALUES (NULL,?,?,MD5(?),?,CURRENT_TIMESTAMP,41)"
        )
        preparedStatement.setString(1, user)
        preparedStatement.setString(2, UUID)
        preparedStatement.setString(3, pass)
        preparedStatement.setString(4, email)
        preparedStatement.executeUpdate()
        Thread.sleep(500)
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT id FROM user WHERE username = ?"
        )
        preparedStatement.setString(1,user)
        rs = preparedStatement.executeQuery()
        val userID = if(rs.next()) {
            rs.getInt("id")
        } else {
            null
        }
        if(userID == null) {
            return 2
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "INSERT INTO inventaire (user_id,inventory) VALUES (?,?)"
        )
        preparedStatement.setInt(1,userID)
        preparedStatement.setString(2,Gson().toJson(Inventory(ArrayList<ItemSlotPair>())))
        preparedStatement.executeUpdate()
        laConnexion.fermerConnexion()
        return 0
    }

    fun createShop(shopName: String, userID: Int): Int {
        val laConnexion = Connexion(url, username, password)
        var boutiqueID = getBoutiqueID(shopName)
        if (boutiqueID != null) {
            laConnexion.fermerConnexion()
            return 1
        }
        var preparedStatement = laConnexion.getConnexion().prepareStatement("INSERT INTO boutiques VALUES (NULL,?,0.0,1200,0)")
        preparedStatement.setString(1, shopName)
        if (preparedStatement.executeUpdate() != 1) {
            laConnexion.fermerConnexion()
            return 1
        }
        boutiqueID = getBoutiqueID(shopName)
        if (boutiqueID == null) {
            laConnexion.fermerConnexion()
            return 1
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement("INSERT INTO membre_boutique VALUES (?,?,1,1200,NOW())")
        preparedStatement.setInt(1, boutiqueID)
        preparedStatement.setInt(2, userID)
        preparedStatement.executeUpdate()
        laConnexion.fermerConnexion()
        return 0
    }

    private fun getBoutiqueID(shopName: String): Int? {
        val laConnexion = Connexion(url, username, password)
        val preparedStatement = laConnexion.getConnexion().prepareStatement("SELECT id FROM boutiques WHERE nom = ?")
        preparedStatement.setString(1, shopName)
        val rs = preparedStatement.executeQuery()
        val id = if (rs.next()) {
            return rs.getInt("id")
        } else {
            null
        }
        laConnexion.fermerConnexion()
        return id
    }

    fun getStores(userID: Int): ArrayList<BoutiqueMembre> {
        val laConnexion = Connexion(url, username, password)
        val storesList = ArrayList<Boutique>()
        var preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT boutiques.id,boutiques.nom,boutiques.solde,boutiques.default_salary,boutiques.nombre_transaction FROM membre_boutique " +
                    "INNER JOIN user ON membre_boutique.user_id = user.id " +
                    "INNER JOIN boutiques ON membre_boutique.boutiques_id = boutiques.id " +
                    "WHERE user_id = ?"
        )
        preparedStatement.setInt(1, userID)
        var rs = preparedStatement.executeQuery()
        while (rs.next()) {
            storesList.add(
                Boutique(
                    rs.getInt("boutiques.id"),
                    rs.getString("boutiques.nom"),
                    rs.getDouble("boutiques.solde"),
                    rs.getDouble("boutiques.default_salary"),
                    rs.getInt("boutiques.nombre_transaction")
                )
            )
        }
        val storesUserList = ArrayList<BoutiqueMembre>()
        storesList.forEach {
            val laConnexion = Connexion("jdbc:mysql://127.0.0.1:3306/boutique", "Plag", "PiWizupAl12u")
            preparedStatement = laConnexion.getConnexion().prepareStatement(
                "SELECT user.id,user.username,user.email,user.password,user.create_time,user.nombre_slot,user.solde,role.id,role.nom_fr,role.nom_en,membre_boutique.salary,membre_boutique.last_salary " +
                        "FROM membre_boutique " +
                        "INNER JOIN boutiques ON boutiques.id = membre_boutique.boutiques_id " +
                        "INNER JOIN user ON user.id = membre_boutique.user_id " +
                        "INNER JOIN role ON role.id = membre_boutique.role_id " +
                        "WHERE boutiques.id = ? AND role.id = 1"
            )
            preparedStatement.setInt(1, it.id)
            rs = preparedStatement.executeQuery()
            while (rs.next()) {
                storesUserList.add(
                    BoutiqueMembre(
                        Boutique(
                            it.id,
                            it.nom,
                            it.solde,
                            it.defaultSalary,
                            it.nombreDeTransaction
                        ),
                        Utilisateur(
                            rs.getInt("user.id"),
                            rs.getString("user.username"),
                            rs.getString("user.email"),
                            rs.getString("user.password"),
                            rs.getTimestamp("user.create_time").toLocalDateTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")),
                            rs.getInt("user.nombre_slot"),
                            rs.getDouble("user.solde")
                        ),
                        Role(
                            rs.getInt("role.id"),
                            rs.getString("role.nom_fr"),
                            rs.getString("role.nom_en")
                        ),
                        rs.getDouble("membre_boutique.salary").toString().trim(),
                        rs.getTimestamp("membre_boutique.last_salary"),
                        null
                    )
                )
            }
            laConnexion.fermerConnexion()
        }
        laConnexion.fermerConnexion()
        return storesUserList
    }

    fun getStores() : ArrayList<Boutique> {
        val laConnexion = Connexion(url, username, password)
        val boutiques = ArrayList<Boutique>()
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT id, nom, solde, default_salary, nombre_transaction FROM boutiques"
        )
        val rs = preparedStatement.executeQuery()
        while (rs.next()) {
            boutiques.add(
                Boutique(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getDouble("solde"),
                    rs.getDouble("default_salary"),
                    rs.getInt("nombre_transaction")
                )
            )
        }
        laConnexion.fermerConnexion()
        return boutiques
    }

    fun giveSalary(member: BoutiqueMembre) {
        val laConnexion = Connexion(url, username, password)
        var preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT solde FROM boutiques INNER JOIN membre_boutique on boutiques.id = membre_boutique.boutiques_id WHERE boutiques_id = ?"
        )
        preparedStatement.setInt(1,member.boutique.id)
        val rs = preparedStatement.executeQuery()
        val solde = if (rs.next()) {
            rs.getDouble("solde")
        } else {
            laConnexion.fermerConnexion()
            System.err.println("La boutique ${member.boutique.id} n'existe pas")
            return
        }
        val salary = member.salary.toDoubleOrNull() ?: return
        if((solde - salary) < 0.0) {
            laConnexion.fermerConnexion()
            System.err.println("The boutique ${member.boutique.nom} don't have enough money to pay the user ${member.utilisateur.username} ()")
            return
        }
        if(member.lastSalary.toLocalDateTime().plusWeeks(1).isAfter(LocalDateTime.now())) {
            laConnexion.fermerConnexion()
            println("\t\t-${member.lastSalary.toLocalDateTime()!!.format(DateTimeFormatter.ofPattern("d/MM/YYYY HH:MM:SS"))}")
            println("\t\t-${LocalDateTime.now()!!.format(DateTimeFormatter.ofPattern("d/MM/YYYY HH:MM:SS"))}")
            println("\t\t-${member.lastSalary.toLocalDateTime().plusWeeks(1).isBefore(LocalDateTime.now())}")
            return
        }

        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE boutiques SET solde = solde - ? WHERE id = ?"
        )
        preparedStatement.setDouble(1,salary)
        preparedStatement.setInt(2,member.boutique.id)
        preparedStatement.executeUpdate()

        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE membre_boutique SET last_salary = NOW() WHERE boutiques_id = ? AND user_id = ?"
        )
        preparedStatement.setInt(1,member.boutique.id)
        preparedStatement.setInt(2,member.utilisateur.id)
        preparedStatement.executeUpdate()

        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE user SET solde = solde + ? WHERE id = ?"
        )
        preparedStatement.setDouble(1,salary)
        preparedStatement.setInt(2,member.utilisateur.id)
        preparedStatement.executeUpdate()
        laConnexion.fermerConnexion()
    }

    fun getStoreMembers(storeID : Int) : ArrayList<BoutiqueMembre> {
        val laConnexion = Connexion(url, username, password)
        val storesUserList = ArrayList<BoutiqueMembre>()
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT boutiques.nom, boutiques.solde, boutiques.default_salary, boutiques.nombre_transaction, user.id,user.username,user.email,user.password,user.create_time,user.nombre_slot,user.solde,role.id,role.nom_fr,role.nom_en,membre_boutique.salary,membre_boutique.last_salary " +
                    "FROM membre_boutique " +
                    "INNER JOIN boutiques ON boutiques.id = membre_boutique.boutiques_id " +
                    "INNER JOIN user ON user.id = membre_boutique.user_id " +
                    "INNER JOIN role ON role.id = membre_boutique.role_id " +
                    "WHERE boutiques.id = ?"
        )
        preparedStatement.setInt(1, storeID)
        val rs = preparedStatement.executeQuery()
        while (rs.next()) {
            storesUserList.add(
                BoutiqueMembre(
                    Boutique(
                        storeID,
                        rs.getString("boutiques.nom"),
                        rs.getDouble("boutiques.solde"),
                        rs.getDouble("boutiques.default_salary"),
                        rs.getInt("boutiques.nombre_transaction")
                    ),
                    Utilisateur(
                        rs.getInt("user.id"),
                        rs.getString("user.username"),
                        rs.getString("user.email"),
                        rs.getString("user.password"),
                        rs.getTimestamp("user.create_time").toLocalDateTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")),
                        rs.getInt("user.nombre_slot"),
                        rs.getDouble("user.solde")
                    ),
                    Role(
                        rs.getInt("role.id"),
                        rs.getString("role.nom_fr"),
                        rs.getString("role.nom_en")
                    ),
                    rs.getDouble("membre_boutique.salary").toString().trim(),
                    rs.getTimestamp("membre_boutique.last_salary"),
                    null
                )
            )
        }
        laConnexion.fermerConnexion()
        return storesUserList
    }

    fun getAllUser(storeID: Int): ArrayList<Utilisateur> {
        val laConnexion = Connexion(url, username, password)
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT user.id,user.username,user.email,user.password,user.create_time,user.nombre_slot,user.solde FROM user " +
                    "LEFT JOIN membre_boutique mb ON user.id = mb.user_id AND mb.boutiques_id = ? " +
                    "WHERE mb.boutiques_id IS NULL"
        )
        preparedStatement.setInt(1,storeID)
        val rs = preparedStatement.executeQuery()
        val users = createUser(rs)
        laConnexion.fermerConnexion()
        return users
    }

    fun getUserRoleID(storeID: Int, userID: Int) : Int? {
        val laConnexion = Connexion(url, username, password)
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT role_id FROM membre_boutique " +
                    "WHERE boutiques_id = ? " +
                    "AND user_id = ?"
        )
        preparedStatement.setInt(1,storeID)
        preparedStatement.setInt(2,userID)
        val rs = preparedStatement.executeQuery()
        val id = if(rs.next()) {
            rs.getInt("role_id")
        } else {
            null
        }
        laConnexion.fermerConnexion()
        return id
    }

    private fun createUser(rs : ResultSet): ArrayList<Utilisateur> {
        val users = ArrayList<Utilisateur>()
        while (rs.next()) {
            users.add(
                Utilisateur(
                    rs.getInt("user.id"),
                    rs.getString("user.username"),
                    rs.getString("user.email"),
                    rs.getString("user.password"),
                    rs.getTimestamp("user.create_time").toLocalDateTime()
                        .format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")),
                    rs.getInt("user.nombre_slot"),
                    rs.getDouble("user.solde"),
                )
            )
        }
        return users
    }

    fun addMember(storeID: Int, username: String): Int {
        val laConnexion = Connexion("jdbc:mysql://127.0.0.1:3306/boutique", "Plag", "PiWizupAl12u")
        var preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT id FROM user WHERE username = ?"
        )
        preparedStatement.setString(1,username)
        var rs = preparedStatement.executeQuery()
        var id: Int
        if(rs.next()) {
            id = rs.getInt("id")
        } else {
            laConnexion.fermerConnexion()
            return 1
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement("SELECT default_salary FROM boutiques WHERE id = ?")
        preparedStatement.setInt(1,storeID)
        rs = preparedStatement.executeQuery()
        val defaultSalary = if(rs.next()) {
            rs.getDouble("default_salary")
        } else {
            0.0
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "INSERT INTO membre_boutique (boutiques_id, user_id, role_id,salary,last_salary) VALUES (?,?,3,?,NOW())"
        )
        preparedStatement.setInt(1,storeID)
        preparedStatement.setInt(2,id)
        preparedStatement.setDouble(3,defaultSalary)
        return if(preparedStatement.executeUpdate() == 1) {
            laConnexion.fermerConnexion()
            0
        } else {
            laConnexion.fermerConnexion()
            1
        }
    }

    fun getAllRoles(members: ArrayList<BoutiqueMembre>, roleID : Int): ArrayList<BoutiqueMembre> {
        val laConnexion = Connexion("jdbc:mysql://127.0.0.1:3306/boutique", "Plag", "PiWizupAl12u")
        val newMembers = ArrayList<BoutiqueMembre>()
        for (member in members) {
            val roles = ArrayList<Role>()
            val preparedStatement = laConnexion.getConnexion().prepareStatement(
                "SELECT id, nom_fr, nom_en FROM role WHERE (id <> 1) OR (1 = ?)"
            )
            preparedStatement.setInt(1, roleID)
            val rs = preparedStatement.executeQuery()
            while (rs.next()) {
                roles.add(
                    Role(
                        rs.getInt("id"),
                        rs.getString("nom_fr"),
                        rs.getString("nom_en")
                    )
                )
            }
            member.missingrole = roles
            newMembers.add(
                member
            )
        }
        laConnexion.fermerConnexion()
        return newMembers
    }

    fun editMember(userID: Int, boutiqueID: Int, roleID: Int) {
        val laConnexion = Connexion(url, username, password)
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE membre_boutique SET role_id = ? WHERE boutiques_id = ? AND user_id = ?"
        )
        preparedStatement.setInt(1,roleID)
        preparedStatement.setInt(2,boutiqueID)
        preparedStatement.setInt(3,userID)
        preparedStatement.executeUpdate()
        laConnexion.fermerConnexion()
    }

    fun removeMember(userID: Int, boutiqueID: Int) {
        val laConnexion = Connexion(url, username, password)
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "DELETE FROM membre_boutique WHERE user_id = ? AND boutiques_id = ?"
        )
        preparedStatement.setInt(1,userID)
        preparedStatement.setInt(2,boutiqueID)
        preparedStatement.executeUpdate()
        laConnexion.fermerConnexion()
    }

    fun getStock(id: Int): ArrayList<Stock> {
        val laConnexion = Connexion(url, username, password)
        val stocks = ArrayList<Stock>()
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT boutiques_id,article_id,quantite,prix,article.nom,article.logo,boutiques.nom,boutiques.solde,boutiques.default_salary, boutiques.nombre_transaction FROM stocks " +
                    "INNER JOIN article ON stocks.article_id = article.id " +
                    "INNER JOIN boutiques ON stocks.boutiques_id = boutiques.id " +
                    "WHERE boutiques_id = ?"
        )
        preparedStatement.setInt(1,id)
        val rs = preparedStatement.executeQuery()
        while (rs.next()) {
            stocks.add(
                Stock(
                    Boutique(
                        rs.getInt("boutiques_id"),
                        rs.getString("boutiques.nom"),
                        rs.getDouble("boutiques.solde"),
                        rs.getDouble("boutiques.default_salary"),
                        rs.getInt("boutiques.nombre_transaction")
                    ),
                    Article(
                        rs.getInt("article_id"),
                        rs.getString("article.nom"),
                        rs.getString("article.logo")
                    ),
                    rs.getInt("quantite"),
                    rs.getDouble("prix")
                )
            )
        }
        laConnexion.fermerConnexion()
        return stocks
    }

    fun debit(id: Int, amount: Double): Int {
        val laConnexion = Connexion(url, username, password)
        var preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT solde FROM user WHERE id = ?"
        )
        preparedStatement.setInt(1,id)
        val rs = preparedStatement.executeQuery()
        val solde = if(rs.next()) {
            rs.getDouble("solde")
        } else {
            0.0
        }
        if((solde - amount) < 0.0) {
            laConnexion.fermerConnexion()
            return 2
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE user SET solde = solde - ? WHERE id = ?"
        )
        preparedStatement.setDouble(1,amount)
        preparedStatement.setInt(2,id)
        val result = preparedStatement.executeUpdate()
        laConnexion.fermerConnexion()
        return result
    }

    fun credit(id: Int, amount: Double): Int {
        val laConnexion = Connexion(url, username, password)
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE user SET solde = solde + ? WHERE id = ?"
        )
        preparedStatement.setDouble(1,amount)
        preparedStatement.setInt(2,id)
        val result = preparedStatement.executeUpdate()
        laConnexion.fermerConnexion()
        return result
    }

    fun pay(fromUser: String, toUser: String, amount: Double): Int {
        val laConnexion = Connexion(url, username, password)
        val solde:Double
        val fromUserID:Int
        val toUserID:Int
        var preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT solde,id FROM user WHERE username = ?"
        )
        preparedStatement.setString(1,fromUser)
        var rs = preparedStatement.executeQuery()
        if(rs.next()) {
            solde = rs.getDouble("solde")
            fromUserID = rs.getInt("id")
        } else {
            laConnexion.fermerConnexion()
            return 2
        }
        if((solde - amount) < 0.0) {
            laConnexion.fermerConnexion()
            return 3
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT id FROM user WHERE username = ?"
        )
        preparedStatement.setString(1,toUser)
        rs = preparedStatement.executeQuery()
        if(rs.next()) {
            toUserID = rs.getInt("id")
        } else {
            laConnexion.fermerConnexion()
            return 4
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE user SET solde = solde - ? WHERE username = ?"
        )
        preparedStatement.setDouble(1,amount)
        preparedStatement.setString(2,fromUser)
        preparedStatement.executeUpdate()
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE user SET solde = solde + ? WHERE username = ?"
        )
        preparedStatement.setDouble(1,amount)
        preparedStatement.setString(2,toUser)
        preparedStatement.executeUpdate()
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "INSERT INTO user_to_user_historique (from_user_id, to_user_id, amount) VALUES (?,?,?)"
        )
        preparedStatement.setInt(1,fromUserID)
        preparedStatement.setInt(2,toUserID)
        preparedStatement.setDouble(3,amount)
        val result = preparedStatement.executeUpdate()
        laConnexion.fermerConnexion()
        return result
    }

    fun payBoutique(user: Int, boutique: Int, amount: Double,username:String, password: String): Int {
        val laConnexion = Connexion(url, this.username, this.password)
        val userConnect = connexion(username,password)
        if(!userConnect.isGoodLogin) {
            laConnexion.fermerConnexion()
            return 5
        }
        val id = userConnect.id

        if(!isTheUserHaveTheRights(id,boutique,2)) {
            laConnexion.fermerConnexion()
            return 5
        }

        val solde:Double

        var preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT solde FROM boutiques WHERE id = ?"
        )
        preparedStatement.setInt(1,boutique)
        var rs = preparedStatement.executeQuery()
        if(rs.next()) {
            solde = rs.getDouble("solde")
        } else {
            laConnexion.fermerConnexion()
            return 2
        }

        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT NULL FROM user WHERE id = ?"
        )
        preparedStatement.setInt(1,user)
        rs = preparedStatement.executeQuery()
        if(!rs.next()) {
            laConnexion.fermerConnexion()
            return 3
        }

        if((solde - amount) < 0.0) {
            laConnexion.fermerConnexion()
            return 4
        }

        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE boutiques SET solde = solde - ? WHERE id = ?"
        )
        preparedStatement.setDouble(1,amount)
        preparedStatement.setInt(2,boutique)
        preparedStatement.executeUpdate()

        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE user SET solde = solde + ? WHERE id = ?"
        )
        preparedStatement.setDouble(1,amount)
        preparedStatement.setInt(2,user)
        preparedStatement.executeUpdate()

        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "INSERT INTO historique (id,boutiques_id, user_id, amount, sens, initiate_user) VALUES (NULL,?,?,?,?,?)"
        )
        preparedStatement.setInt(1,boutique)
        preparedStatement.setInt(2,user)
        preparedStatement.setDouble(3,amount)
        preparedStatement.setString(4,"->")
        preparedStatement.setInt(5,id)
        val result = preparedStatement.executeUpdate()
        laConnexion.fermerConnexion()
        return result
    }

    fun isTheUserHaveTheRights(userID: Int, boutiqueID: Int, userMinimumRank: Int): Boolean {
        val boutiques = getStores(userID)
        for (boutique in boutiques) {
            if (boutique.boutique.id == boutiqueID) {
                return boutique.role.id <= userMinimumRank
            }
        }
        return false
    }

    fun editSalary(salary: Double, boutiqueId: Int, userID: Int): Int {
        val laConnexion = Connexion(url, username, password)
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE membre_boutique SET salary = ? WHERE boutiques_id = ? AND user_id = ?"
        )
        preparedStatement.setDouble(1,salary)
        preparedStatement.setInt(2,boutiqueId)
        preparedStatement.setInt(3,userID)
        val result = preparedStatement.executeUpdate()
        laConnexion.fermerConnexion()
        return result
    }

    fun deleteStand(id: Int) {
        val laConnexion = Connexion(url, username, password)
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "DELETE FROM boutiques WHERE id = ?"
        )
        preparedStatement.setInt(1,id)
        preparedStatement.executeUpdate()
        laConnexion.fermerConnexion()
    }

    fun getUserRolesID(stores: ArrayList<BoutiqueMembre>, userID: Int): ArrayList<Int> {
        val roles = ArrayList<Int>()
        for (store in stores) {
            roles.add(getUserRoleID(store.boutique.id,userID)!!)
        }
        return roles
    }

    fun editStock(articleID: Int, boutiqueID: Int, price: Double) {
        val laConnexion = Connexion(url, username, password)
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE stocks SET prix = ? WHERE article_id = ? AND boutiques_id = ?"
        )
        preparedStatement.setDouble(1,price)
        preparedStatement.setInt(2,articleID)
        preparedStatement.setInt(3,boutiqueID)
        preparedStatement.executeUpdate()
        laConnexion.fermerConnexion()
    }

    fun addStock(articleID: Int, boutiqueID: Int, quantity: Int, price: Double) {
        val laConnexion = Connexion(url, username, password)
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "INSERT INTO stocks (boutiques_id,article_id,quantite,prix) VALUES (?,?,?,?)"
        )
        preparedStatement.setInt(1,boutiqueID)
        preparedStatement.setInt(2,articleID)
        preparedStatement.setInt(3,quantity)
        preparedStatement.setDouble(4,price)
        preparedStatement.executeUpdate()
        laConnexion.fermerConnexion()
    }

    fun bailoutStock(articleID: Int, boutiqueID: Int, quantity: Int) {
        val laConnexion = Connexion(url, username, password)
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE stocks SET article_id = ?, boutiques_id = ?, quantite = quantite + ?"
        )
        preparedStatement.setInt(1,boutiqueID)
        preparedStatement.setInt(2,articleID)
        preparedStatement.setInt(3,quantity)
        preparedStatement.executeUpdate()
        laConnexion.fermerConnexion()
    }

    fun removeItemFromInventory(userID: Int, item: Item, amount: Int): Boolean {
        val laConnexion = Connexion(url, username, password)
        var preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT inventory FROM inventaire WHERE user_id = ?"
        )
        preparedStatement.setInt(1,userID)
        val rs = preparedStatement.executeQuery()
        var inventory = if(rs.next()) {
            rs.getString("inventory")
        } else {
            null
        }
        if(inventory == null) {
            return false
        }

        var itemInInv: Item? = null
        var pos: Int? = null

        val inv = Gson().fromJson(inventory, Inventory::class.java)

        for (i in 0..inv.items.size) {
            if(inv.items[i].item.material.name.equals(item.material.name)) {
                itemInInv = inv.items[i].item
                pos = i
            }
        }

        if(itemInInv == null || pos == null) {
            return false
        }

        if((itemInInv.quantity - amount) < 0) {
            return false
        }
        itemInInv.quantity -= amount;
        inv.items[pos].item = itemInInv

        inventory = Gson().toJson(inv)

        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE inventaire SET inventory = ? WHERE user_id = ?"
        )
        preparedStatement.setString(1,inventory)
        preparedStatement.setInt(2,userID)
        preparedStatement.executeUpdate()
        laConnexion.fermerConnexion()
        return true
    }

    fun updateInventory(inventory: ArrayList<ItemSlotPair>, username: String) {
        val laConnexion = Connexion(url, this.username, password)
        var preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT id FROM user WHERE username = ?"
        )
        preparedStatement.setString(1,username)
        val rs = preparedStatement.executeQuery()
        val userID = if(rs.next()) {
            rs.getInt("id")
        } else {
            null
        }
        if(userID == null) {
            return
        }
        for(i in 0 until inventory.size) {
            val item = inventory[i].item
            addItemIfNotExist(item)
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE inventaire SET inventory = ? WHERE user_id = ?"
        )
        preparedStatement.setString(1, Gson().toJson(Inventory(inventory)))
        preparedStatement.setInt(2,userID)
        preparedStatement.executeUpdate()
        laConnexion.fermerConnexion()
    }

    private fun addItemIfNotExist(item: Item) {
        val laConnexion = Connexion(url, username, password)
        var preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT NULL FROM article WHERE nom = ?"
        )
        preparedStatement.setString(1,item.material.name)
        val rs = preparedStatement.executeQuery()
        if(rs.next()) {
            return
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "INSERT INTO article (id, nom, logo) VALUES (NULL, ?, ?)"
        )
        preparedStatement.setString(1,item.material.name)
        preparedStatement.setString(2,"/assets/img/item/${item.material.name.toLowerCase()}.png")
        preparedStatement.executeUpdate()
        laConnexion.fermerConnexion()
    }

    fun getSolde(username: String): Double {
        var solde: Double
        val laConnexion = Connexion(url, this.username, password)
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT solde FROM user WHERE username = ?"
        )
        preparedStatement.setString(1,username)
        val rs = preparedStatement.executeQuery()
        if(rs.next()) {
            solde = rs.getDouble("solde")
        } else {
            solde = -1.0
        }
        laConnexion.fermerConnexion()
        return solde
    }

    fun getInventory(username: String): Inventory? {
        val id:Int = getUtilisateurID(username)
        if(id < 0) {
            return null
        }
        val laConnexion = Connexion(url, this.username, password)
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT inventory FROM inventaire WHERE user_id = ?"
        )
        preparedStatement.setInt(1,id)
        val rs = preparedStatement.executeQuery()
        val inventory: Inventory? = if(rs.next()) {
            Gson().fromJson(rs.getString("inventory"), Inventory::class.java)
        } else {
            null
        }
        laConnexion.fermerConnexion()
        return inventory
    }

    private fun getUtilisateurID(username: String): Int {
        val laConnexion = Connexion(url, this.username, password)
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT id FROM user WHERE username = ?"
        )
        preparedStatement.setString(1,username)
        val rs = preparedStatement.executeQuery()
        var id = -1
        if(rs.next()) {
            id = rs.getInt("id")
        }
        laConnexion.fermerConnexion()
        return id
    }

    fun getItemImage(item: Item) : String? {
        val laConnexion = Connexion(url, username, password)
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT logo FROM article WHERE nom = ?"
        )
        preparedStatement.setString(1,item.material.name)
        val rs = preparedStatement.executeQuery()
        var imageSrc = if(rs.next()) {
            println(rs.getString("logo"))
            rs.getString("logo")
        } else {
            null
        }
        return imageSrc
    }

    fun getAllStores() : ArrayList<Boutique> {
        val laConnexion = Connexion(url, username, password)
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT id, nom, solde, default_salary,nombre_transaction FROM boutiques"
        )
        val rs = preparedStatement.executeQuery()
        val boutiques = ArrayList<Boutique>()
        while (rs.next()) {
            boutiques.add(
                Boutique(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getDouble("solde"),
                    rs.getDouble("default_salary"),
                    rs.getInt("nombre_transaction")
                )
            )
        }
        laConnexion.fermerConnexion()
        return boutiques
    }

    fun getNumberOfItem(storeID: Int): Int {
        val laConnexion = Connexion(url, username, password)
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT COUNT(*) FROM stocks WHERE boutiques_id = ?"
        )
        preparedStatement.setInt(1,storeID)
        val rs = preparedStatement.executeQuery()
        val count = if(rs.next()) {
            rs.getInt(1)
        } else {
            0
        }
        laConnexion.fermerConnexion()
        return count
    }

    fun getNumberOfMember(storeID: Int): Int {
        val laConnexion = Connexion(url, username, password)
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT COUNT(*) FROM membre_boutique WHERE boutiques_id = ?"
        )
        preparedStatement.setInt(1,storeID)
        val rs = preparedStatement.executeQuery()
        val count = if(rs.next()) {
            rs.getInt(1)
        } else {
            0
        }
        laConnexion.fermerConnexion()
        return count
    }

    fun getShopOwner(storeID: Int): Utilisateur? {
        val laConnexion = Connexion(url, username, password)
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT id, username, email, password, create_time, nombre_slot, solde " +
            "FROM membre_boutique " +
            "INNER JOIN user ON user.id = membre_boutique.user_id " +
            "WHERE boutiques_id = ? AND role_id = 1"
        )
        preparedStatement.setInt(1,storeID)
        val rs = preparedStatement.executeQuery()
        val user = if(rs.next()) {
            Utilisateur(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("email"),
                "",
                rs.getTimestamp("create_time").toLocalDateTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")),
                rs.getInt("nombre_slot"),
                rs.getDouble("solde")
            )
        } else {
            null
        }
        laConnexion.fermerConnexion()
        return user
    }

    fun getShopRating(shopID: Int): Double? {
        val laConnexion = Connexion(url, username, password)
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT ((SUM(rating)/(COUNT(rating)*5)) * 5), COUNT(*) FROM rating WHERE boutiques_id = ?"
        )
        preparedStatement.setInt(1,shopID)
        val rs = preparedStatement.executeQuery()
        val rating = if(rs.next()) {
            if(rs.getInt(2) > 0) {
                rs.getDouble(1)
            } else {
                null
            }
        } else {
            null
        }
        laConnexion.fermerConnexion()
        return rating
    }

    fun addReview(shopID: Int, commentary: String, rating: Double, userID: Int): Boolean {
        val laConnexion = Connexion(url, username, password)
        var preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT NULL FROM rating WHERE user_id = ? AND boutiques_id = ?"
        )
        preparedStatement.setInt(1,userID)
        preparedStatement.setInt(2,shopID)
        val rs = preparedStatement.executeQuery()
        if(rs.next()) {
            return false
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "INSERT INTO rating (boutiques_id, user_id, rating, commentary) VALUES (?,?,?,?)"
        )
        preparedStatement.setInt(1,shopID)
        preparedStatement.setInt(2,userID)
        preparedStatement.setDouble(3,rating)
        preparedStatement.setString(4,commentary)
        return preparedStatement.executeUpdate() == 1
    }

    fun getUserRating(shopID: Int, userID: Int): Rating {
        val laConnexion = Connexion(url, username, password)
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT rating,commentary FROM rating WHERE user_id = ? AND boutiques_id = ?"
        )
        preparedStatement.setInt(1,userID)
        preparedStatement.setInt(2,shopID)
        val rs = preparedStatement.executeQuery()
        val rating = if(rs.next()) {
            Rating(
                rs.getDouble("rating"),
                rs.getString("commentary")
            )
        } else {
            Rating(
                0.0,
                ""
            )
        }
        laConnexion.fermerConnexion()
        return rating
    }
}