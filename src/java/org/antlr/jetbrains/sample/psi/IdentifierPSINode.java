package org.antlr.jetbrains.sample.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import org.antlr.jetbrains.adaptor.lexer.RuleIElementType;
import org.antlr.jetbrains.adaptor.psi.Trees;
import org.antlr.jetbrains.sample.SampleElementRef;
import org.antlr.jetbrains.sample.SampleLanguage;
import org.antlr.jetbrains.sample.SampleParserDefinition;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static org.antlr.jetbrains.sample.parser.SampleLanguageParser.RULE_call_expr;
import static org.antlr.jetbrains.sample.parser.SampleLanguageParser.RULE_formal_arg;
import static org.antlr.jetbrains.sample.parser.SampleLanguageParser.RULE_function;
import static org.antlr.jetbrains.sample.parser.SampleLanguageParser.RULE_statement;
import static org.antlr.jetbrains.sample.parser.SampleLanguageParser.RULE_vardef;

/** From doc: "Every element which can be renamed or referenced
 *             needs to implement com.intellij.psi.PsiNamedElement interface."
 *
 *  So, all leaf nodes that represent variables, functions, classes, or
 *  whatever in your plugin language must be instances of this not just
 *  LeafPsiElement.  Your ASTFactory should create this kind of object for
 *  ID tokens. This node is for references *and* definitions because you can
 *  highlight a function and say "jump to definition". Also we want defs
 *  to be included in "find usages."
 *  PsiNameIdentifierOwner implementations are the corresponding subtree roots
 *  that define symbols.
 *
 *  You can click on an ID in the editor and ask for a rename for all nodes
 *  of this type.
 */
public class IdentifierPSINode extends LeafPsiElement implements PsiNamedElement {
	public IdentifierPSINode(IElementType type, CharSequence text) {
		super(type, text);
	}

	@Override
	public String getName() {
		return getText();
	}

	/** "Return the element corresponding to this element after the rename (either <code>this</code>
	 *   or a different element if the rename caused the element to be replaced)."
	 */
	@Override
	public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
		if ( getParent()==null ) return this; // weird but it happened once
		IElementType elType = getParent().getNode().getElementType();
		String kind = "??? ";
		if ( elType instanceof RuleIElementType ) {
			int ruleIndex = ((RuleIElementType) elType).getRuleIndex();
			if ( ruleIndex == RULE_call_expr ) {
				kind = "call ";
			}
			else if ( ruleIndex == RULE_statement ) {
				kind = "assign ";
			}
			else if ( ruleIndex == RULE_function ) {
				kind = "func def ";
			}
		}
		System.out.println("IdentifierPSINode.setName("+name+") on "+kind+this+" at "+Integer.toHexString(this.hashCode()));
		PsiElement newID = Trees.createLeafFromText(getProject(),
		                                            SampleLanguage.INSTANCE,
		                                            getContext(),
		                                            name,
		                                            SampleParserDefinition.ID);
		if ( newID!=null ) {
			return this.replace(newID); // use replace on leaves but replaceChild on ID nodes that are part of defs/decls.
		}
		return this;
	}

	@Override
	public PsiReference getReference() {
		PsiElement parent = getParent();
		IElementType elType = parent.getNode().getElementType();
		// do not return a reference for the ID nodes in a definition
		if ( elType instanceof RuleIElementType ) {
			int ruleIndex = ((RuleIElementType) elType).getRuleIndex();
			switch ( ruleIndex ) {
				case RULE_function:
				case RULE_vardef:
				case RULE_formal_arg:
					return null;
			}
		}
		return new SampleElementRef(this);
	}
}
