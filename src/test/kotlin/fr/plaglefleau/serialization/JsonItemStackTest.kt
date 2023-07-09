package fr.plaglefleau.serialization

import com.google.gson.Gson
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import kotlin.test.Test
import kotlin.test.assertEquals

class JsonItemStackTest {

    @Test
    fun fromJson() {
        val json1 = "{\"type\":\"IRON_SWORD\",\"data\":57,\"item-meta\":{}}"
        val itemStack1 = ItemStack(Material.IRON_SWORD)
        itemStack1.amount = 1
        val itemMeta = itemStack1.itemMeta as Damageable
        itemMeta.damage = 57
        itemStack1.itemMeta = itemMeta
        val json2 = "{\"type\":\"TERRACOTTA\"}"
        val itemStack2 = ItemStack(Material.TERRACOTTA)
        itemStack2.amount = 1
        val json3 = "{\"type\":\"TORCH\",\"amount\":62}"
        val itemStack3 = ItemStack(Material.TORCH)
        itemStack3.amount = 62

        assertEquals(itemStack1, JsonItemStack.fromJson(json1), "formJson test 1")
        assertEquals(itemStack2, JsonItemStack.fromJson(json2), "formJson test 2")
        assertEquals(itemStack3, JsonItemStack.fromJson(json3), "formJson test 3")
    }

    @Test
    fun toJson() {
        val json1 = "{\"type\":\"IRON_SWORD\",\"data\":57,\"item-meta\":{}}"
        val itemStack1 = ItemStack(Material.IRON_SWORD)
        itemStack1.amount = 1
        val itemMeta = itemStack1.itemMeta as Damageable
        itemMeta.damage = 57
        itemStack1.itemMeta = itemMeta
        val json2 = "{\"type\":\"TERRACOTTA\"}"
        val itemStack2 = ItemStack(Material.TERRACOTTA)
        itemStack2.amount = 1
        val json3 = "{\"type\":\"TORCH\",\"amount\":62}"
        val itemStack3 = ItemStack(Material.TORCH)
        itemStack3.amount = 62

        assertEquals(json1, JsonItemStack.toJson(itemStack1), "toJson test 1")
        assertEquals(json2, JsonItemStack.toJson(itemStack2), "toJson test 1")
        assertEquals(json3, JsonItemStack.toJson(itemStack3), "toJson test 1")
    }
}