package org.antlr.jetbrains.sample.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import org.antlr.jetbrains.adaptor.lexer.PSIElementTypeFactory;
import org.antlr.jetbrains.adaptor.lexer.TokenIElementType;
import org.antlr.jetbrains.adaptor.psi.Trees;
import org.antlr.jetbrains.sample.SampleLanguage;
import org.antlr.jetbrains.sample.parser.SampleLanguageLexer;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SamplePSIRefNode extends LeafPsiElement implements PsiNamedElement {
	protected String name = null; // an override to input text ID if we rename via intellij

	public SamplePSIRefNode(IElementType type, CharSequence text) {
		super(type, text);
	}

	@Override
	public String getName() {
		if ( name!=null ) return name;
		return getText();
	}

	@Override
	public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
		/*
		From doc: "Creating a fully correct AST node from scratch is
		          quite difficult. Thus, surprisingly, the easiest way to
		          get the replacement node is to create a dummy file in the
		          custom language so that it would contain the necessary
		          node in its parse tree, build the parse tree and
		          extract the necessary node from it.
		 */
		System.out.println("rename "+this+" to "+name);
		List<TokenIElementType> tokenIElementTypes =
			PSIElementTypeFactory.getTokenIElementTypes(SampleLanguage.INSTANCE);
		PsiElement newNode = Trees.createLeafFromText(getProject(),
		                                              SampleLanguage.INSTANCE,
		                                              getContext(),
		                                              name, tokenIElementTypes.get(SampleLanguageLexer.ID));
		if ( newNode!=null ) {
			this.replace(newNode);
			this.name = name;
			return this;
		}
		throw new IncorrectOperationException();
	}
}
