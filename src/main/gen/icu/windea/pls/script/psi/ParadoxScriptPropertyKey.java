// This is a generated file. Not intended for manual editing.
package icu.windea.pls.script.psi;

import com.intellij.navigation.*;
import com.intellij.psi.*;
import icu.windea.pls.core.expression.*;
import icu.windea.pls.core.psi.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface ParadoxScriptPropertyKey extends ParadoxExpressionAwareElement, StubBasedPsiElement<ParadoxScriptPropertyKeyStub> {

  @NotNull
  List<ParadoxScriptParameter> getParameterList();

  @NotNull
  String getValue();

  @NotNull
  ParadoxScriptPropertyKey setValue(@NotNull String value);

  @Nullable
  PsiReference getReference();

  @NotNull
  PsiReference[] getReferences();

  @NotNull
  ParadoxDataType getExpressionType();

  @Nullable
  String getConfigExpression();

  @Nullable
  ItemPresentation getPresentation();

}
