// This is a generated file. Not intended for manual editing.
package icu.windea.pls.cwt.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralValue;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiComment;

public class CwtVisitor extends PsiElementVisitor {

  public void visitBlock(@NotNull CwtBlock o) {
    visitValue(o);
    // visitBlockElement(o);
  }

  public void visitBoolean(@NotNull CwtBoolean o) {
    visitValue(o);
    // visitPsiLiteralValue(o);
  }

  public void visitDocumentationComment(@NotNull CwtDocumentationComment o) {
    visitPsiComment(o);
  }

  public void visitDocumentationText(@NotNull CwtDocumentationText o) {
    visitPsiElement(o);
  }

  public void visitFloat(@NotNull CwtFloat o) {
    visitValue(o);
    // visitPsiLiteralValue(o);
  }

  public void visitInt(@NotNull CwtInt o) {
    visitValue(o);
    // visitPsiLiteralValue(o);
  }

  public void visitOption(@NotNull CwtOption o) {
    visitNamedElement(o);
  }

  public void visitOptionComment(@NotNull CwtOptionComment o) {
    visitPsiComment(o);
  }

  public void visitOptionKey(@NotNull CwtOptionKey o) {
    visitPsiElement(o);
  }

  public void visitProperty(@NotNull CwtProperty o) {
    visitNamedElement(o);
  }

  public void visitPropertyKey(@NotNull CwtPropertyKey o) {
    visitPsiLiteralValue(o);
    // visitNavigatablePsiElement(o);
  }

  public void visitRootBlock(@NotNull CwtRootBlock o) {
    visitBlockElement(o);
  }

  public void visitString(@NotNull CwtString o) {
    visitValue(o);
    // visitNamedElement(o);
    // visitPsiLiteralValue(o);
  }

  public void visitValue(@NotNull CwtValue o) {
    visitNavigatablePsiElement(o);
  }

  public void visitNavigatablePsiElement(@NotNull NavigatablePsiElement o) {
    visitElement(o);
  }

  public void visitPsiComment(@NotNull PsiComment o) {
    visitElement(o);
  }

  public void visitPsiLiteralValue(@NotNull PsiLiteralValue o) {
    visitElement(o);
  }

  public void visitBlockElement(@NotNull CwtBlockElement o) {
    visitPsiElement(o);
  }

  public void visitNamedElement(@NotNull CwtNamedElement o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
