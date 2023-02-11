// This is a generated file. Not intended for manual editing.
package icu.windea.pls.script.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralValue;
import com.intellij.psi.ContributedReferenceHost;
import com.intellij.psi.PsiReference;
import icu.windea.pls.core.expression.ParadoxDataType;

public interface ParadoxScriptInt extends ParadoxScriptValue, PsiLiteralValue, ContributedReferenceHost {

  @NotNull
  String getName();

  @NotNull
  String getValue();

  @NotNull
  ParadoxScriptValue setValue(@NotNull String name);

  int getIntValue();

  @NotNull
  ParadoxDataType getType();

  @Nullable
  PsiReference getReference();

  @NotNull
  PsiReference[] getReferences();

}
