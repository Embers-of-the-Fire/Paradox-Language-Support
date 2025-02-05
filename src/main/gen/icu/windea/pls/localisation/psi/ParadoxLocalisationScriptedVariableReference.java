// This is a generated file. Not intended for manual editing.
package icu.windea.pls.localisation.psi;

import com.intellij.navigation.*;
import com.intellij.psi.search.*;
import icu.windea.pls.core.psi.*;
import icu.windea.pls.core.references.*;
import icu.windea.pls.model.*;
import org.jetbrains.annotations.*;
import icu.windea.pls.core.psi.ParadoxScriptedVariableReference;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import icu.windea.pls.model.ParadoxType;
import icu.windea.pls.core.references.ParadoxScriptedVariablePsiReference;
import javax.swing.Icon;

import javax.swing.*;

public interface ParadoxLocalisationScriptedVariableReference extends ParadoxScriptedVariableReference {

  @NotNull
  Icon getIcon(@IconFlags int flags);

  @Nullable
  String getName();

  @NotNull
  ParadoxLocalisationScriptedVariableReference setName(@NotNull String name);

  @Nullable
  ParadoxScriptedVariablePsiReference getReference();

  @NotNull
  ParadoxType getType();

  @NotNull
  String getExpression();

  @NotNull
  ItemPresentation getPresentation();

  @NotNull
  GlobalSearchScope getResolveScope();

  @NotNull
  SearchScope getUseScope();

}
