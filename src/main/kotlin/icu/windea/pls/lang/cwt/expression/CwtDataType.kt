package icu.windea.pls.lang.cwt.expression

import icu.windea.pls.core.annotations.*
import icu.windea.pls.model.*

enum class CwtDataType {
    Block,
    Bool,
    Int,
    Float,
    Scalar,
    ColorField,
    PercentageField,
    DateField,
    Localisation,
    SyncedLocalisation,
    InlineLocalisation,
    Definition,
    //EXTENDED BY PLS
    AbsoluteFilePath,
    Icon,
    FilePath,
    //EXTENDED BY PLS
    FileName,
    EnumValue,
    Value,
    ValueSet,
    ValueOrValueSet,
    ScopeField,
    Scope,
    ScopeGroup,
    ValueField,
    IntValueField,
    VariableField,
    IntVariableField,
    Modifier, //<modifier>
    Parameter, //$parameter
    ParameterValue, //$parameter_value
    LocalisationParameter, //$localisation_parameter
    SingleAliasRight,
    AliasName,
    AliasKeysField,
    AliasMatchLeft,
    Template,
    Constant,
    Any,
    Other,
    @WithGameType(ParadoxGameType.Stellaris)
    StellarisNameFormat,
    //EXTENDED BY PLS
    /** 对应`.shader`文件中的effect。 */
    ShaderEffect;
    
    //modify implementation of below methods should also check codes that directly based on enum constants
    //so they are just as a convenience
    
    fun isIntType() = this == Int || this == IntValueField || this == IntVariableField
    
    fun isFloatType() = this == Int || this == Float || this == ValueField || this == IntValueField || this == VariableField || this == IntVariableField
    
    fun isPathReferenceType() = this == AbsoluteFilePath || this == FileName || this == FilePath || this == Icon
    
    fun isScopeFieldType() = this == ScopeField || this == Scope || this == ScopeGroup
    
    fun isValueFieldType() = this == ValueField || this == IntValueField
    
    fun isVariableFieldType() = this == VariableField || this == IntVariableField
    
    fun isValueSetValueType() = this == Value || this == ValueSet || this == ValueOrValueSet
    
    fun isConstantLikeType() = this == Constant || this == Template
    
    fun isKeyReferenceType() = this == Bool || this == Int || this == Float || this == Scalar || this == Constant || this == Any
}