package org.antlr.jetbrains.sample.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public abstract class SampleElementRef extends PsiReferenceBase<IdentifierPSINode> {
	public SampleElementRef(@NotNull IdentifierPSINode element) {
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
		String refName = myElement.getName();
		System.out.println(getClass().getSimpleName()+".isReferenceTo("+refName+"->"+def.getText()+")");
		// sometimes def comes in pointing to ID node itself. weird
		if ( def instanceof IdentifierPSINode && isDefSubtree(def.getParent()) ) {
			def = def.getParent();
		}
		if ( isDefSubtree(def) ) {
			PsiElement id = ((PsiNameIdentifierOwner)def).getNameIdentifier();
			String defName = id!=null ? id.getText() : null;
			return refName!=null && defName!=null && refName.equals(defName);
		}
		return false;
	}

	/** Is the def a subtree associated with refs to getElement()'s kind of node? */
	public abstract boolean isDefSubtree(PsiElement def);
}
