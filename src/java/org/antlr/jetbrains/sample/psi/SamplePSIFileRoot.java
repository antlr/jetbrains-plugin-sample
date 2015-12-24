package org.antlr.jetbrains.sample.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.tree.IElementType;
import org.antlr.jetbrains.adaptor.lexer.RuleIElementType;
import org.antlr.jetbrains.adaptor.psi.ANTLRPsiNode;
import org.antlr.jetbrains.adaptor.psi.ScopeNode;
import org.antlr.jetbrains.adaptor.psi.Trees;
import org.antlr.jetbrains.adaptor.xpath.XPath;
import org.antlr.jetbrains.sample.Icons;
import org.antlr.jetbrains.sample.SampleFileType;
import org.antlr.jetbrains.sample.SampleLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;

import static org.antlr.jetbrains.sample.parser.SampleLanguageParser.RULE_call_expr;
import static org.antlr.jetbrains.sample.parser.SampleLanguageParser.RULE_expr;
import static org.antlr.jetbrains.sample.parser.SampleLanguageParser.RULE_primary;
import static org.antlr.jetbrains.sample.parser.SampleLanguageParser.RULE_statement;

public class SamplePSIFileRoot extends PsiFileBase implements ScopeNode {
    public SamplePSIFileRoot(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, SampleLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return SampleFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Sample Language file";
    }

    @Override
    public Icon getIcon(int flags) {
        return Icons.SAMPLE_ICON;
    }

	/** Return null since a file scope has no enclosing scope. It is
	 *  not itself in a scope.
	 */
	@Override
	public ScopeNode getContext() {
		return null;
	}

	@Nullable
    @Override
    public PsiElement resolve(PsiNamedElement element) {
        ANTLRPsiNode root = Trees.getRoot(element);
        PsiElement parent = element.getParent();
        IElementType elType = parent.getNode().getElementType();
        if ( !(elType instanceof RuleIElementType) ) return null;

        switch ( ((RuleIElementType)elType).getRuleIndex() ) {
            case RULE_call_expr :
                Collection<? extends PsiElement> nameNodes =
                    XPath.findAll(SampleLanguage.INSTANCE, root, "/script/function/ID");
                String id = element.getName();
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
}
