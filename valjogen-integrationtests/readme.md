VALue Java Objects Generator (VALJOGen) - Integration Tests
===========================================================

#About this module
This module contains a number of known annotated source files that the annotation processor is run against in order to produce new generated source files.
These generated source files then compared with pre-verified expected results that acts as a "golden master". When a generated source file is different from the master
it is assumed there is an regression error. If this is not the case the golden master must be updated manually.

In addition this project also checks some of the generated source files for additonal correctness using the nl.jqno.equalsverifier tool.

#Author and contact info
**Morten M. Christensen, [mmc (AT) 41concepts |dot| com](www.41concepts.com)**
