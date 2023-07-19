package fr.plaglefleau.models.minecraft

data class Item(val material: Material, val displayName: String, var quantity:Int, val damage:Int, val enchantments : ArrayList<Enchantment>, var imageSrc: String?, var shulkerInventory: ArrayList<ItemSlotPair>?)