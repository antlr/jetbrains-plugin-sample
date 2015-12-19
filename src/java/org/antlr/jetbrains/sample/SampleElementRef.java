package org.antlr.jetbrains.sample;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import org.antlr.jetbrains.adaptor.lexer.PSIElementTypeFactory;
import org.antlr.jetbrains.adaptor.lexer.RuleIElementType;
import org.antlr.jetbrains.adaptor.lexer.TokenIElementType;
import org.antlr.jetbrains.adaptor.psi.ANTLRPsiNodeAdaptor;
import org.antlr.jetbrains.adaptor.psi.Trees;
import org.antlr.jetbrains.adaptor.xpath.XPath;
import org.antlr.jetbrains.sample.parser.SampleLanguageLexer;
import org.antlr.jetbrains.sample.psi.IdentifierPSINode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static org.antlr.jetbrains.sample.parser.SampleLanguageParser.RULE_call_expr;
import static org.antlr.jetbrains.sample.parser.SampleLanguageParser.RULE_expr;
import static org.antlr.jetbrains.sample.parser.SampleLanguageParser.RULE_formal_arg;
import static org.antlr.jetbrains.sample.parser.SampleLanguageParser.RULE_function;
import static org.antlr.jetbrains.sample.parser.SampleLanguageParser.RULE_primary;
import static org.antlr.jetbrains.sample.parser.SampleLanguageParser.RULE_statement;
import static org.antlr.jetbrains.sample.parser.SampleLanguageParser.RULE_vardef;

public class SampleElementRef extends PsiReferenceBase<IdentifierPSINode> {
	protected String id;
	public SampleElementRef(@NotNull IdentifierPSINode element, String id) {
		/** WARNING: You must send up the text range or you get this error:
		 * "Cannot find manipulator for PsiElement(ID) in org.antlr.jetbrains.sample.SampleElementRef"...
		 *  when you click on an identifier.  During rename you get this
		 *  error too if you don't impl handleElementRename().
		 *
		 *  Wow. This also solved the rename issue that was causing GUI thread
		 *  to deadlock after the rename.
		 *
		 *  The range is relative to start of the token; I guess for
		 *  qualified references we might want to use just a part of the name.
		 */
		super(element, new TextRange(0, id.length()));
		this.id = id;
	}

	@NotNull
	@Override
	public Object[] getVariants() {
		return new Object[0];
	}

	@Nullable
	@Override
	public PsiElement resolve() {
		ANTLRPsiNodeAdaptor root = Trees.getRoot(myElement);
		PsiElement parent = myElement.getParent();
		IElementType elType = parent.getNode().getElementType();
		if ( !(elType instanceof RuleIElementType) ) return null;

		switch ( ((RuleIElementType)elType).getRuleIndex() ) {
			case RULE_function :
			case RULE_vardef :
			case RULE_formal_arg :
				return myElement; // we're at the def already
			case RULE_call_expr :
				Collection<? extends PsiElement> nameNodes =
					XPath.findAll(SampleLanguage.INSTANCE, root, "/script/function/ID");
				PsiElement idNode = Trees.toMap(nameNodes).get(id);
				return idNode;
//				if ( idNode!=null ) {
//					return idNode.getParent(); // return function node
//				}
			case RULE_statement :
			case RULE_expr :
			case RULE_primary :
				break;
			default :
		}
		return null;
	}

	/** Return value appears to be ignored. Without this method, we get an error.
	 *  I also note that myElement.replace(newNode) works but
	 *  myElement.getNode().replaceChild(idNode, newID.getNode()) does not.
	 *  No error but the item is not actually changed in the document.
	 */
	@Override
	public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
		System.out.println(getClass().getSimpleName()+".handleElementRename("+myElement.getText()+"->"+newElementName+
			                   ") on "+myElement+" at "+Integer.toHexString(myElement.hashCode()));
//		ASTNode idNode = myElement.getNode().findChildByType(SampleParserDefinition.ID);
//		if (idNode != null) {
//			PsiElement newID = Trees.createLeafFromText(myElement.getProject(),
//			                                            SampleLanguage.INSTANCE,
//			                                            myElement.getContext(),
//			                                            newElementName,
//			                                            SampleParserDefinition.ID);
//			if ( newID!=null ) {
//				myElement.getNode().replaceChild(idNode, newID.getNode());
//			}
//		}
//		return myElement;

		Project project = getElement().getProject();
		List<TokenIElementType> tokenIElementTypes =
			PSIElementTypeFactory.getTokenIElementTypes(SampleLanguage.INSTANCE);
		PsiElement newNode = Trees.createLeafFromText(project,
		                                              SampleLanguage.INSTANCE,
		                                              myElement.getContext(),
		                                              newElementName,
		                                              tokenIElementTypes.get(SampleLanguageLexer.ID));
//		System.out.println("createLeafFromText() creates "+newNode+" at "+Integer.toHexString(newNode.hashCode()));
		newNode = myElement.replace(newNode);
//		System.out.println("handleElementRename("+newElementName+") returns "+newNode+" at "+Integer.toHexString(newNode.hashCode()));
		return newNode;
	}

	@Override
	public boolean isReferenceTo(PsiElement element) {
		PsiElement def = resolve();
		return def!=null && def.getText().equals(element.getText());
	}
}
