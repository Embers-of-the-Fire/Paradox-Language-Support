package icu.windea.pls.model;

enum class ParadoxSeparatorType(
    val text: String
) {
    EQUAL("="),
    NOT_EQUAL("!="),
    LT("<"),
    GT(">"),
    LE("<="),
    GE(">=");
    
    override fun toString(): String {
        return text
    }
}