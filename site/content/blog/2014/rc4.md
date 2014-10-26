---
kind: article
created_at: 26 October 2014
created_by: mmc
title: RC4 and a bug in Eclipse
tags: [Release]
---

New release candidate 4 of VALJOGen uploaded in maven central with following changes:

+ Made processor run inside Eclipse - [found 1 bug in eclipse though][eclipsebug].
+ Windows file bugfix.
+ Support for javac's <code>-processorpath</code>
+ New processor option <code>com.fortyoneconcepts.valjogen.LOGFILE</code>
+ Dependencies to annotations/processors now optional in pom's.
+ Updated documentation and website.

* * *

### Eclipse bug 382590

Eclipse is not using Javac's backend so it has it's own implementation of <code>javax.lang.model</code> which is different and buggy.

Consequently, I made a few changes in the annotation processor to fit eclipse's implementation but ultimately VALJOGen was hit by [Eclipse bug 382590][eclipsebug] which happens when subclasssing a generic interface.

So for some generic use cases, users will now see an error message refering to [Eclipse bug 382590][eclipsebug]. For other use cases the processor works inside eclipse without problems.

For now, if you need generic subclasses and you are using Eclipse you should pre-generate VALJOGen classes with maven, ant, javac or a script. This should be easy if you use a multi-stage build with code generation from interfaces in firste stage (recommended and not only as a workaround).

**This particular bug in Eclipse was reported in 2012 and has not been fixed yet. If you want VALJOGen and other annotation processors to work perfectly in eclipse then [please cast your vote for the bug at Eclipse's bugzilla][eclipsebug].**

[eclipsebug]: https://bugs.eclipse.org/bugs/show_bug.cgi?id=382590  "Eclipse bug 382590"