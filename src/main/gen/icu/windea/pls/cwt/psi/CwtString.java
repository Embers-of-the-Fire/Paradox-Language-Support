// This is a generated file. Not intended for manual editing.
package icu.windea.pls.cwt.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralValue;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Iconable.IconFlags;
import com.intellij.psi.search.SearchScope;
import javax.swing.Icon;

public interface CwtString extends CwtValue, CwtNamedElement, PsiLiteralValue {

  @NotNull
  Icon getIcon(@IconFlags int flags);

  @NotNull
  String getName();

  @NotNull
  CwtString setName(@NotNull String name);

  @NotNull
  PsiElement getNameIdentifier();

  @NotNull
  String getValue();

  @NotNull
  CwtString setValue(@NotNull String value);

  @NotNull
  String getStringValue();

  @NotNull
  ItemPresentation getPresentation();

  @NotNull
  SearchScope getUseScope();

}
