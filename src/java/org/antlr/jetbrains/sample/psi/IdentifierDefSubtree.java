package org.antlr.jetbrains.sample.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.IncorrectOperationException;
import org.antlr.jetbrains.adaptor.psi.ANTLRPsiNodeAdaptor;
import org.antlr.jetbrains.sample.SampleParserDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The superclass of nodes in your PSI tree that come from ANTLR subtree
 *  roots and that represent phrases that define/declare variables, functions, ...
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

	@Nullable
	@Override
	public PsiElement getNameIdentifier() {
		ASTNode idNode = getNode().findChildByType(SampleParserDefinition.ID);
		if (idNode != null) {
			return idNode.getPsi();
		}
		return null;
	}

	/** This must indicate where the ID node associated with this def subtree starts */
	@Override
	public int getTextOffset() {
		PsiElement id = getNameIdentifier();
		return id!=null ? id.getTextOffset() : super.getTextOffset();
	}

	@Override
	public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
		System.out.println(getClass().getSimpleName()+".setName("+name+") on "+
			                   this+" at "+Integer.toHexString(this.hashCode()));
		PsiNamedElement idNode = (PsiNamedElement)getNameIdentifier();
		if ( idNode!=null ) {
			return idNode.setName(name);
		}
		return this;
	}
}
