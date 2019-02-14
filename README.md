# Sample IntelliJ plugin using ANTLR grammar

This is a demonstration of [ANTLRv4 library for IntelliJ plugins](https://github.com/antlr/antlr4-intellij-adaptor/), 
which makes it easy to create plugins for IntelliJ-based IDEs based on an ANTLRv4 grammar.

<img src=screenshot.png>

## Running the plugin for the first time

Make sure the Gradle plugin is installed in your IDE, go to `File -> Open`, select the `build.gradle` file
and choose `Open as Project`. 

If you already imported the project when it was not based on Gradle, then choose the option to delete the existing 
project and reimport it.

Once the IDE is done downloading dependencies and refreshing the project, you can use the `Gradle` tool window
and use the following `Tasks`:
* `build > assemble` to build the project
* `intellij > runIde` to run the plugin in a sandboxed instance

## Noteworthy things

### Gradle build
The build is based on Gradle, and uses the [gradle-intellij-plugin](https://github.com/JetBrains/gradle-intellij-plugin),
which makes it easy to:

* pull dependencies, especially the IntelliJ SDK and `antlr4-intellij-adaptor`
* build and run tests in a CI environment on different versions of the SDK
* generate lexers & parsers from your grammars, thanks to the [ANTLR plugin for Gradle](https://docs.gradle.org/current/userguide/antlr_plugin.html)
* publish plugins to the [JetBrains Plugins Repository](https://plugins.jetbrains.com/)
* configure the project for occasional contributors 🙂

### ANTLRPsiNode

PSI nodes defined in the plugin extend `ANTLRPsiNode` and `IdentifierDefSubtree`, which automatically
makes them `PsiNameIdentifierOwner`s.

### Error highlighting

Errors are shown by `SampleExternalAnnotator`, which makes use of `org.antlr.intellij.adaptor.xpath.XPath` to
detect references to unknown functions.

### ParserDefinition

`SampleParserDefinition` uses several handy classes from the adaptor library:

* `PSIElementTypeFactory` to generate `IElementType`s from tokens and rules defined in your ANTLRv4 grammar
* `ANTLRLexerAdaptor` to bind generated lexers to a `com.intellij.lexer.Lexer`
* `ANTLRParserAdaptor` to bind generated parsers to a `com.intellij.lang.PsiParser`

## Misc

**WARNING**. Turn on Dragon speech recognition for Mac and do a rename.
GUI deadlocks.  Every time. Turn off dragon. No problem ever.
See [JetBrains forum](https://devnet.jetbrains.com/message/5566967#5566967).
