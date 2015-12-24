package org.antlr.jetbrains.sample.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class VardefSubtree extends IdentifierDefSubtree {
	public VardefSubtree(@NotNull ASTNode node) {
		super(node);
	}
}
