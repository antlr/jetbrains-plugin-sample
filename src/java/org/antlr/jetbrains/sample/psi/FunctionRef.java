package org.antlr.jetbrains.sample.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.antlr.jetbrains.adaptor.lexer.RuleIElementType;
import org.antlr.jetbrains.adaptor.psi.ANTLRPsiNodeAdaptor;
import org.antlr.jetbrains.adaptor.psi.Trees;
import org.antlr.jetbrains.adaptor.xpath.XPath;
import org.antlr.jetbrains.sample.SampleLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static org.antlr.jetbrains.sample.parser.SampleLanguageParser.RULE_call_expr;
import static org.antlr.jetbrains.sample.parser.SampleLanguageParser.RULE_expr;
import static org.antlr.jetbrains.sample.parser.SampleLanguageParser.RULE_primary;
import static org.antlr.jetbrains.sample.parser.SampleLanguageParser.RULE_statement;

/** A reference object associated with a IdentifierPSINode underneath a
 *  call_expr rule subtree root.
 */
public class FunctionRef extends SampleElementRef {
	public FunctionRef(@NotNull IdentifierPSINode element) {
		super(element);
	}

	/** Resolve a reference to the definition subtree, do not resolve to the ID
	 *  child of the subtree root.
	 */
	@Nullable
	@Override
	public PsiElement resolve() {
//		System.out.println(getClass().getSimpleName()+
//			                   ".resolve("+myElement.getName()+
//			                   " at "+Integer.toHexString(myElement.hashCode())+")");

		ANTLRPsiNodeAdaptor root = Trees.getRoot(myElement);
		PsiElement parent = myElement.getParent();
		IElementType elType = parent.getNode().getElementType();
		if ( !(elType instanceof RuleIElementType) ) return null;

		switch ( ((RuleIElementType)elType).getRuleIndex() ) {
			case RULE_call_expr :
				Collection<? extends PsiElement> nameNodes =
					XPath.findAll(SampleLanguage.INSTANCE, root, "/script/function/ID");
				String id = myElement.getName();
				PsiElement idNode = Trees.toMap(nameNodes).get(id); // Find identifier node of function definition
				if ( idNode!=null ) {
					return idNode.getParent(); // resolve to function subtree root
				}
			case RULE_statement :
			case RULE_expr :
			case RULE_primary :
				break;
			default :
		}
		return null;
	}

	@Override
	public boolean isDefSubtree(PsiElement def) {
		return def instanceof FunctionSubtree;
	}
}
