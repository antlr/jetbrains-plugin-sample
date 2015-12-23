package org.antlr.jetbrains.sample.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.IncorrectOperationException;
import org.antlr.jetbrains.sample.SampleParserDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FunctionSubtree extends IdentifierDefSubtree {
	public FunctionSubtree(@NotNull ASTNode node) {
		super(node);
	}

	@Override
	public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
		System.out.println("FunctionSubtree.setName("+name+") on "+this+" at "+Integer.toHexString(this.hashCode()));
		ASTNode idNode = getNode().findChildByType(SampleParserDefinition.ID);
		if (idNode != null) {
			// delegate to the ID child to setName().
			return ((PsiNamedElement)idNode.getPsi()).setName(name);
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
