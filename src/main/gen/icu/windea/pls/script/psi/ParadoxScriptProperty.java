// This is a generated file. Not intended for manual editing.
package icu.windea.pls.script.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Iconable.IconFlags;
import com.intellij.psi.SmartPsiElementPointer;
import icu.windea.pls.script.expression.ParadoxScriptExpressionType;
import java.util.Map;
import java.util.Set;
import javax.swing.Icon;

public interface ParadoxScriptProperty extends ParadoxScriptNamedElement, ParadoxScriptTypedElement, ParadoxDefinitionProperty, StubBasedPsiElement<ParadoxScriptPropertyStub> {

  @NotNull
  ParadoxScriptPropertyKey getPropertyKey();

  @Nullable
  ParadoxScriptPropertyValue getPropertyValue();

  @NotNull
  Icon getIcon(@IconFlags int flags);

  @NotNull
  String getName();

  @NotNull
  ParadoxScriptProperty setName(@NotNull String name);

  @Nullable
  PsiElement getNameIdentifier();

  @Nullable
  String getValue();

  int getDepth();

  @Nullable
  ParadoxScriptBlock getBlock();

  @Nullable
  String getDefinitionType();

  @Nullable
  ParadoxScriptExpressionType getExpressionType();

  @NotNull
  String getConditionExpression();

  @Nullable
  String getConfigExpression();

  @Nullable
  String getPathName();

  @NotNull
  String getOriginalPathName();

  @NotNull
  Map<String, Set<SmartPsiElementPointer<ParadoxParameter>>> getParameterMap();

  @NotNull
  ItemPresentation getPresentation();

}
