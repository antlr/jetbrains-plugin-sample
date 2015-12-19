package org.antlr.jetbrains.sample.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import org.antlr.jetbrains.adaptor.psi.ANTLRPsiNodeAdaptor;
import org.jetbrains.annotations.NotNull;

/** The superclass of nodes in your PSI tree that come from ANTLR subtree
 *  roots and that represent phrases that define/declare variables, functions, ...
 *
 *  Currently, I've hooked this in, but I'm not sure what if anything is
 *  using getNameIdentifier() or why ANTLRPsiNodeAdaptor isn't good enough.
 */
public abstract class IdentifierDefSubtree extends ANTLRPsiNodeAdaptor implements PsiNameIdentifierOwner {
	public IdentifierDefSubtree(@NotNull ASTNode node) {
		super(node);
	}

	@Override
	public String getName() {
		PsiElement id = getNameIdentifier();
		return id!=null ? id.getText() : null;
	}
}
