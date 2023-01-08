// This is a generated file. Not intended for manual editing.
package icu.windea.pls.cwt.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import icu.windea.pls.config.cwt.CwtSeparatorType;
import javax.swing.Icon;

public interface CwtOption extends CwtNamedElement {

  @NotNull
  CwtOptionKey getOptionKey();

  @Nullable
  CwtValue getOptionValue();

  @NotNull
  Icon getIcon(@IconFlags int flags);

  @NotNull
  String getName();

  @NotNull
  CwtOption setName(@NotNull String name);

  @NotNull
  PsiElement getNameIdentifier();

  @Nullable
  String getValue();

  @NotNull
  CwtSeparatorType getSeparatorType();

}
