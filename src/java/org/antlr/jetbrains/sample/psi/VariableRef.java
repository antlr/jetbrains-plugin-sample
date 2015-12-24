package org.antlr.jetbrains.sample.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VariableRef extends SampleElementRef {
	public VariableRef(@NotNull IdentifierPSINode element) {
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
		return null;
	}

	@Override
	public boolean isDefSubtree(PsiElement def) {
		return def instanceof VardefSubtree;
	}
}
