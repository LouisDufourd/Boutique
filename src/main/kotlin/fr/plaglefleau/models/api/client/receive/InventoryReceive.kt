package fr.plaglefleau.models.api.client.receive

import fr.plaglefleau.models.minecraft.ItemSlotPair

data class InventoryReceive(val inventory: ArrayList<ItemSlotPair>, val username: String)
