package fr.plaglefleau.bdd_MySql

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GestionTest {

    val gestion = Gestion()
    @Test
    fun connexion() {
        assertTrue("connection test 1", gestion.connexion("PlagLeFleau","PlagLeFleau0745").isGoodLogin)
        assertFalse("connection test 2", gestion.connexion("PlagLeFleau","").isGoodLogin)
        assertFalse("connection test 3", gestion.connexion("PlagLeFleau","plag").isGoodLogin)
        assertFalse("connection test 4", gestion.connexion("Plag","PlagLeFleau0745").isGoodLogin)
        assertFalse("connection test 5", gestion.connexion("","PlagLeFleau0745").isGoodLogin)
        assertFalse("connection test 6", gestion.connexion("","").isGoodLogin)
    }

    @Test
    fun getUtilisateur() {
        assertNotNull(gestion.getUtilisateur(13), "getUtilisateur test 1")
        assertNull(gestion.getUtilisateur(-1), "getUtilisateur test 2")
    }
}