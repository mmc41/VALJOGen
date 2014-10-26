# Example of compiling examples using plain javac on *nix - assumes valjogen-annotationprocessor has been build already by maven.

#!/bin/sh
BASEDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
mkdir -p ${BASEDIR}/target/cmdline-generatedoutput
mkdir -p ${BASEDIR}/target/cmdline-classes

javac -parameters -cp ../valjogen-annotations/target/valjogen-annotations-1.0.0-RC4.jar -processorpath ../valjogen-processor/target/valjogen-annotationprocessor-1.0.0-RC4.jar -Acom.fortyoneconcepts.valjogen.SOURCEPATH=${BASEDIR}/src/main/java -s ${BASEDIR}/target/cmdline-generatedoutput -d ${BASEDIR}/target/cmdline-classes ${BASEDIR}/src/main/java/com/fortyoneconcepts/valjogen/examples/*.java

#javac -parameters -cp ../valjogen-processor/target/valjogen-annotationprocessor-1.0.0-RC4.jar -Acom.fortyoneconcepts.valjogen.SOURCEPATH=${BASEDIR}/src/main/java -s ${BASEDIR}/target/cmdline-generatedoutput -d ${BASEDIR}/target/cmdline-classes ${BASEDIR}/src/main/java/com/fortyoneconcepts/valjogen/examples/*.java
