// This is a generated file. Not intended for manual editing.
package icu.windea.pls.script.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.openapi.util.Iconable.IconFlags;
import icu.windea.pls.core.expression.ParadoxDataType;
import javax.swing.Icon;

public interface ParadoxScriptValue extends ParadoxScriptExpressionElement {

  @NotNull
  Icon getIcon(@IconFlags int flags);

  @NotNull
  String getValue();

  @NotNull
  ParadoxDataType getExpressionType();

  @Nullable
  String getConfigExpression();

}
