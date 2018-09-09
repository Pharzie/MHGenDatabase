package com.ghstudios.android.features.armorsetbuilder.armorselect

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.ghstudios.android.data.classes.Armor
import com.ghstudios.android.data.database.DataManager
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.util.loggedThread


// todo: move somewhere else, have the ASB handle the mapping instead
fun getSlotForPieceIndex(pieceIndex: Int) = when(pieceIndex) {
    0 -> Armor.ARMOR_SLOT_HEAD
    1 -> Armor.ARMOR_SLOT_BODY
    2 -> Armor.ARMOR_SLOT_ARMS
    3 -> Armor.ARMOR_SLOT_WAIST
    4 -> Armor.ARMOR_SLOT_LEGS
    else -> ""
}

class ArmorSelectViewModel(private val app: Application): AndroidViewModel(app) {
    private val dataManager = DataManager.get()

    val allArmorData = MutableLiveData<List<ArmorGroup>>()

    fun initialize(asbIndex: Int, hunterType: Int) {
        val armorSlot = getSlotForPieceIndex(asbIndex)
        val rarityLevels = app.resources.getStringArray(R.array.armor_rarity_levels)

        loggedThread("Armor Select armor loading") {
            val armorSkillPoints = dataManager.queryArmorSkillPointsByType(armorSlot, hunterType)
            val allArmorItems = armorSkillPoints.groupBy { it.armor.rarity }.toSortedMap().map {
                val rarity = it.key
                val armorPieces = it.value

                val labelName = rarityLevels[rarity-1]
                ArmorGroup(labelName, armorPieces)
            }
            allArmorData.postValue(allArmorItems)
        }
    }
}