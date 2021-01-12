// This is a generated file. Not intended for manual editing.
package com.windea.plugin.idea.paradox.localisation.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.openapi.util.Iconable.IconFlags;
import com.windea.plugin.idea.paradox.localisation.reference.ParadoxLocalisationCommandFieldPsiReference;
import javax.swing.Icon;

public interface ParadoxLocalisationCommandField extends ParadoxLocalisationNamedElement {

  @Nullable
  ParadoxLocalisationPropertyReference getPropertyReference();

  @Nullable
  PsiElement getCommandFieldToken();

  @Nullable
  String getName();

  @NotNull
  PsiElement setName(@NotNull String name);

  void checkRename();

  @Nullable
  PsiElement getNameIdentifier();

  @Nullable
  ParadoxLocalisationCommandFieldPsiReference getReference();

  @NotNull
  Icon getIcon(@IconFlags int flags);

}
