package icu.windea.pls.model

import icu.windea.pls.core.*

/**
 * @param entries 对于此游戏类型，匹配CWT规则时，也需要基于哪些子目录。
 */
enum class ParadoxGameType(
    val id: String,
    val title: String,
    val steamId: String,
    val entries: List<String> = emptyList(),
) {
    Stellaris("stellaris", "Stellaris", "281990", listOf("pdx_launcher/game", "pdx_launcher/common", "pdx_online_assets", "previewer_assets")),
    Ck2("ck2", "Crusader Kings II", "203770", listOf("jomini")),
    Ck3("ck3", "Crusader Kings III", "1158310", listOf("jomini")),
    Eu4("eu4", "Europa Universalis IV", "236850", listOf("jomini")),
    Hoi4("hoi4", "Hearts of Iron IV", "394360", listOf("jomini")),
    Ir("ir", "Imperator: Rome", "859580", listOf("jomini")),
    Vic2("vic2", "Victoria II", "42960", listOf("jomini")),
    Vic3("vic3", "Victoria III", "529340", listOf("jomini"));
    
    override fun toString(): String {
        return title
    }
    
    companion object {
        val values = values()
        val valueList = values.toList()
        
        private val valueMap = values.associateBy { it.id }
        private val valueMapByTitle = values.associateBy { it.title }
        private val valueMapBySteamId = values.associateBy { it.steamId }
        
        fun resolve(id: String) = valueMap[id.lowercase()]
        
        fun resolveByTitle(title: String) = valueMapByTitle[title]
        
        fun resolveBySteamId(steamId: String) = valueMapBySteamId[steamId]
        
        fun placeholder() = Stellaris
    }
}

val ParadoxGameType?.id get() = this?.id ?: "core"

val ParadoxGameType?.linkToken get() = if(this == null) "" else "${id}:"

fun ParadoxGameType?.orDefault() = this ?: getSettings().defaultGameType
