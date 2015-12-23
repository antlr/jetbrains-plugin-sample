package org.antlr.jetbrains.sample;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import org.antlr.jetbrains.adaptor.lexer.RuleIElementType;
import org.antlr.jetbrains.adaptor.psi.ANTLRPsiNodeAdaptor;
import org.antlr.jetbrains.adaptor.psi.Trees;
import org.antlr.jetbrains.adaptor.xpath.XPath;
import org.antlr.jetbrains.sample.psi.FunctionSubtree;
import org.antlr.jetbrains.sample.psi.IdentifierPSINode;
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
public class SampleFunctionRef extends PsiReferenceBase<IdentifierPSINode> {
	public SampleFunctionRef(@NotNull IdentifierPSINode element) {
		/** WARNING: You must send up the text range or you get this error:
		 * "Cannot find manipulator for PsiElement(ID) in org.antlr.jetbrains.sample.SampleElementRef"...
		 *  when you click on an identifier.  During rename you get this
		 *  error too if you don't impl handleElementRename().
		 *
		 *  The range is relative to start of the token; I guess for
		 *  qualified references we might want to use just a part of the name.
		 *  Or we might look inside string literals for stuff.
		 */
		super(element, new TextRange(0, element.getText().length()));
	}

	@NotNull
	@Override
	public Object[] getVariants() {
		return new Object[0];
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

	/** Change the REFERENCE's ID node (not the targeted def's ID node)
	 *  to reflect a rename.
	 *
	 *  Without this method, we get an error.
	 *
	 *  getElement() refers to the identifier node that references the definition.
	 */
	@Override
	public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
		System.out.println(getClass().getSimpleName()+".handleElementRename("+myElement.getName()+"->"+newElementName+
			                   ") on "+myElement+" at "+Integer.toHexString(myElement.hashCode()));

		return myElement.setName(newElementName);
	}

	@Override
	public boolean isReferenceTo(PsiElement def) {
		if ( def==myElement ) return false; // ref is never a ref to itself
		String refName = myElement.getName();
		String defName = def.getText();
		if ( def instanceof FunctionSubtree ) {
			ASTNode idNode = def.getNode().findChildByType(SampleParserDefinition.ID);
			if ( idNode!=null ) {
				defName = idNode.getText();
			}
		}
		System.out.println(getClass().getSimpleName()+".isReferenceTo("+refName+"->"+defName+")");
		return refName!=null && defName!=null && refName.equals(defName);
	}
}
