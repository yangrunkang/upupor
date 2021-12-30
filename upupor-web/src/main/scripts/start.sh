#!/bin/bash
#
# MIT License
#
# Copyright (c) 2021 yangrunkang
#
# Author: yangrunkang
# Email: yangrunkang53@gmail.com
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.
#

cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
CONF_DIR=$DEPLOY_DIR/conf

SERVER_NAME=upupor-web

if [ -z "$SERVER_NAME" ]; then
    SERVER_NAME=`hostname`
fi

PIDS=`ps -ef | grep java | grep "$CONF_DIR" |awk '{print $2}'`
if [ -n "$PIDS" ]; then
    echo "ERROR: The $SERVER_NAME already started!"
    echo "PID: $PIDS"
    exit 1
fi

LIB_DIR=$DEPLOY_DIR/lib
LIB_JARS=`ls $LIB_DIR|grep .jar|awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`

JAVA_OPTS=" -Djava.net.preferIPv4Stack=true -Dfile.encoding=utf-8"
JAVA_DEBUG_OPTS=""
if  [ "$1" = "debug" ]; then
    JAVA_DEBUG_OPTS=" -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n "
fi
JAVA_JMX_OPTS=" -Dcom.sun.management.jmxremote -Djava.rmi.server.hostname=127.0.0.1 -Dcom.sun.management.jmxremote.port=11502 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false "
JAVA_MEM_OPTS=""
JAVA_APP_NAME=" -Dapplication.name=upupor-web "
BITS=`java -version 2>&1 | grep -i 64-bit`
if [ -n "$BITS" ]; then
	JAVA_MEM_OPTS=" -server -Xmx600m -Xms600m -Xmn192m -XX:PermSize=128m -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:./logs/gc.log -XX:HeapDumpPath=./logs/dump.hprof "
else
    JAVA_MEM_OPTS=" -server -Xmx600m -Xms600m -Xmn192m -XX:PermSize=128m -Xss256k -XX:SurvivorRatio=2 -XX:+UseParallelGC "
fi

echo -e "Starting the $SERVER_NAME ...\c"
nohup java $JAVA_APP_NAME $JAVA_OPTS $JAVA_MEM_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS -classpath $CONF_DIR:$LIB_JARS com.upupor.web.UpuporWebApplication 2>&1 &

COUNT=0
while [ $COUNT -lt 1 ]; do    
    echo -e ".\c"
    sleep 1 
    COUNT=`ps -f | grep java | grep "$DEPLOY_DIR" | awk '{print $2}' | wc -l`
    if [ $COUNT -gt 0 ]; then
        break
    fi
done

echo "OK!"
PIDS=`ps -f | grep java | grep "$DEPLOY_DIR" | awk '{print $2}'`
echo "PID: $PIDS"
