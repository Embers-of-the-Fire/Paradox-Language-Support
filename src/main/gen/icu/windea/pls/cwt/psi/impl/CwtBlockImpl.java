// This is a generated file. Not intended for manual editing.
package icu.windea.pls.cwt.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static icu.windea.pls.cwt.psi.CwtElementTypes.*;
import icu.windea.pls.cwt.psi.*;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Iconable.IconFlags;
import com.intellij.psi.search.SearchScope;
import javax.swing.Icon;

public class CwtBlockImpl extends CwtValueImpl implements CwtBlock {

  public CwtBlockImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull CwtVisitor visitor) {
    visitor.visitBlock(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CwtVisitor) accept((CwtVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<CwtDocumentationComment> getDocumentationCommentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CwtDocumentationComment.class);
  }

  @Override
  @NotNull
  public List<CwtOption> getOptionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CwtOption.class);
  }

  @Override
  @NotNull
  public List<CwtOptionComment> getOptionCommentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CwtOptionComment.class);
  }

  @Override
  @NotNull
  public List<CwtProperty> getPropertyList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CwtProperty.class);
  }

  @Override
  @NotNull
  public List<CwtValue> getValueList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CwtValue.class);
  }

  @Override
  @NotNull
  public Icon getIcon(@IconFlags int flags) {
    return CwtPsiImplUtil.getIcon(this, flags);
  }

  @Override
  @NotNull
  public String getName() {
    return CwtPsiImplUtil.getName(this);
  }

  @Override
  @NotNull
  public String getValue() {
    return CwtPsiImplUtil.getValue(this);
  }

  @Override
  public boolean isEmpty() {
    return CwtPsiImplUtil.isEmpty(this);
  }

  @Override
  public boolean isNotEmpty() {
    return CwtPsiImplUtil.isNotEmpty(this);
  }

  @Override
  @NotNull
  public List<PsiElement> getComponents() {
    return CwtPsiImplUtil.getComponents(this);
  }

  @Override
  @NotNull
  public ItemPresentation getPresentation() {
    return CwtPsiImplUtil.getPresentation(this);
  }

  @Override
  @NotNull
  public SearchScope getUseScope() {
    return CwtPsiImplUtil.getUseScope(this);
  }

}
