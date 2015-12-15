package org.antlr.jetbrains.sample;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.antlr.jetbrains.adaptor.lexer.ANTLRLexerAdaptor;
import org.antlr.jetbrains.adaptor.lexer.PSIElementTypeFactory;
import org.antlr.jetbrains.adaptor.parser.ANTLRParserAdaptor;
import org.antlr.jetbrains.adaptor.psi.ANTLRPsiNodeAdaptor;
import org.antlr.jetbrains.sample.parser.SampleLanguageLexer;
import org.antlr.jetbrains.sample.parser.SampleLanguageParser;
import org.antlr.jetbrains.sample.psi.SamplePSIFileRoot;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jetbrains.annotations.NotNull;

public class SampleParserDefinition implements ParserDefinition {
	public static final IFileElementType FILE =
		new IFileElementType(SampleLanguage.INSTANCE);

	static {
		PSIElementTypeFactory.defineLanguageIElementTypes(SampleLanguage.INSTANCE,
		                                                  SampleLanguageParser.tokenNames,
		                                                  SampleLanguageParser.ruleNames);
	}

	public static final TokenSet COMMENTS =
		PSIElementTypeFactory.createTokenSet(
			SampleLanguage.INSTANCE,
			SampleLanguageLexer.COMMENT,
			SampleLanguageLexer.LINE_COMMENT);

	public static final TokenSet WHITESPACE =
		PSIElementTypeFactory.createTokenSet(
			SampleLanguage.INSTANCE,
			SampleLanguageLexer.WS);

	public static final TokenSet STRING =
		PSIElementTypeFactory.createTokenSet(
			SampleLanguage.INSTANCE,
			SampleLanguageLexer.STRING);

	@NotNull
	@Override
	public Lexer createLexer(Project project) {
		SampleLanguageLexer lexer = new SampleLanguageLexer(null);
		return new ANTLRLexerAdaptor(SampleLanguage.INSTANCE, lexer);
	}

	@NotNull
	public PsiParser createParser(final Project project) {
		final SampleLanguageParser parser = new SampleLanguageParser(null);
		return new ANTLRParserAdaptor(SampleLanguage.INSTANCE, parser) {
			@Override
			protected ParseTree parse(Parser parser, IElementType root) {
				return ((SampleLanguageParser)parser).script();
			}
		};
	}

	/** "Tokens of those types are automatically skipped by PsiBuilder." */
	@NotNull
	public TokenSet getWhitespaceTokens() {
		return WHITESPACE;
	}

	@NotNull
	public TokenSet getCommentTokens() {
		return COMMENTS;
	}

	@NotNull
	public TokenSet getStringLiteralElements() {
		return STRING;
	}

	public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
		return SpaceRequirements.MAY;
	}

	/** What is the IFileElementType of the root parse tree node? It
	 *  is called from {@link #createFile(FileViewProvider)} at least.
	 */
	@Override
	public IFileElementType getFileNodeType() {
		return FILE;
	}

	/** Create the root of your PSI tree (a PsiFile).
	 *
	 *  From IntelliJ IDEA Architectural Overview:
	 *  "A PSI (Program Structure Interface) file is the root of a structure
	 *  representing the contents of a file as a hierarchy of elements
	 *  in a particular programming language."
	 *
	 *  PsiFile is to be distinguished from a FileASTNode, which is a parse
	 *  tree node that eventually becomes a PsiFile. From PsiFile, we can get
	 *  it back via: {@link PsiFile#getNode}.
	 */
	@Override
	public PsiFile createFile(FileViewProvider viewProvider) {
		return new SamplePSIFileRoot(viewProvider);
	}

	/** Convert from *internal* parse node (AST they call it) to final PSI node. This
	 *  converts only internal rule nodes apparently, not leaf nodes. Leaves
	 *  are just tokens I guess.
	 *
	 *  If you don't care to distinguish PSI nodes by type, it is sufficient
	 *  to create a {@link ASTWrapperPsiElement} around the parse tree node
	 *  (ASTNode in jetbrains speak).
	 */
	@NotNull
	public PsiElement createElement(ASTNode node) {
		return new ANTLRPsiNodeAdaptor(node);
	}
}
