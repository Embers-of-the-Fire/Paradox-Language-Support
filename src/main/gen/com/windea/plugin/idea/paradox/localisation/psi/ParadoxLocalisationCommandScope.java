// This is a generated file. Not intended for manual editing.
package com.windea.plugin.idea.paradox.localisation.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.openapi.util.Iconable.IconFlags;
import com.windea.plugin.idea.paradox.localisation.reference.ParadoxLocalisationCommandScopePsiReference;
import javax.swing.Icon;

public interface ParadoxLocalisationCommandScope extends ParadoxLocalisationNamedElement {

  @NotNull
  PsiElement getCommandScopeToken();

  @NotNull
  String getName();

  @NotNull
  PsiElement setName(@NotNull String name);

  void checkRename();

  @NotNull
  PsiElement getNameIdentifier();

  @NotNull
  ParadoxLocalisationCommandScopePsiReference getReference();

  @NotNull
  Icon getIcon(@IconFlags int flags);

}
