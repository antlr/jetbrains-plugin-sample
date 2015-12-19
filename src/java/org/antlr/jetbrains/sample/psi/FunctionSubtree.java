package org.antlr.jetbrains.sample.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.antlr.jetbrains.adaptor.psi.Trees;
import org.antlr.jetbrains.sample.SampleLanguage;
import org.antlr.jetbrains.sample.SampleParserDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Currently, I've hooked this in, but I'm not sure what if anything is
 *  using getNameIdentifier() or why ANTLRPsiNodeAdaptor isn't good enough.
 */
public class FunctionSubtree extends IdentifierDefSubtree {
	public FunctionSubtree(@NotNull ASTNode node) {
		super(node);
	}

	@Override
	public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
		System.out.println("FunctionSubtree.setName("+name+") on "+this+" at "+Integer.toHexString(this.hashCode()));
		ASTNode idNode = getNode().findChildByType(SampleParserDefinition.ID);
		if (idNode != null) {
			PsiElement newID = Trees.createLeafFromText(getProject(),
			                                              SampleLanguage.INSTANCE,
			                                              getContext(),
			                                              name,
			                                              SampleParserDefinition.ID);
			if ( newID!=null ) {
				getNode().replaceChild(idNode, newID.getNode());
			}
		}
		return this;
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
}
