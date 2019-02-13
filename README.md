# Sample Jetbrains plugin using ANTLR grammar

This is a demonstration of [ANTLR-jetbrains supported library](https://github.com/antlr/jetbrains).

<img src=screenshot.png>

**WARNING**. Turn on Dragon speech recognition for Mac and do a rename.
GUI deadlocks.  Every time. Turn off dragon. No problem ever.
See [jetbrains forum](https://devnet.jetbrains.com/message/5566967#5566967).

## Running the plugin for the first time

You need to fetch the antlr dependencies from the maven pom file with `mvn dependency:resolve`. To process the 
grammar files, call `mvn process-resources`.

Go to `File -> Project Structure -> Project` and add a new `IntelliJ Platform Plugin SDK` or use a valid existing one.

You also need the jetbrains antlr adapter. It may be added with `git submodule init` and `git submodule update`.

Now create a new `Run Configuration` of the type `Plugin`. 
