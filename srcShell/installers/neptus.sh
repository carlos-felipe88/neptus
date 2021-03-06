#!/bin/bash
PROGNAME=$0
NEPTUS_HOME=`dirname $PROGNAME`
cd $NEPTUS_HOME

CLASSPATH=".:bin/neptus.jar:conf@NEPTUS_LIBS@":$CLASSPATH

LIBS=".:libJNI"

if test -d jre/bin; then JAVA_BIN_FOLDER="jre/bin/"; else JAVA_BIN_FOLDER=""; fi

JAVA_MACHINE_TYPE=$($JAVA_BIN_FOLDER"java" -cp bin/neptus.jar pt.lsts.neptus.loader.helper.CheckJavaOSArch)
if [ ${JAVA_MACHINE_TYPE} == 'x64' ]; then
 LIBS=".:libJNI/x64:libJNI:/usr/lib/jni:/usr/lib/vtk-5.10"
else
  LIBS=".:libJNI/x86:libJNI:/usr/lib/jni:/usr/lib/vtk-5.10"
fi

export VMFLAGS="-XX:+HeapDumpOnOutOfMemoryError"

export LD_LIBRARY_PATH=".:libJNI"
$JAVA_BIN_FOLDER"java" -Xms10m -Xmx1024m $VMFLAGS -Djava.library.path=".:libJNI" -cp $CLASSPATH pt.lsts.neptus.loader.NeptusMain "$@"
