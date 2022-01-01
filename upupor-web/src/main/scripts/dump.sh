#!/bin/bash
#
# MIT License
#
# Copyright (c) 2021-2022 yangrunkang
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
if [ -z "$PIDS" ]; then
    echo "ERROR: The $SERVER_NAME does not started!"
    exit 1
fi

DUMP_DIR=$DEPLOY_DIR/dump
if [ ! -d $DUMP_DIR ]; then
	mkdir $DUMP_DIR
fi
DUMP_DATE=`date +%Y%m%d%H%M%S`
DATE_DIR=$DUMP_DIR/$DUMP_DATE
if [ ! -d $DATE_DIR ]; then
	mkdir $DATE_DIR
fi

echo -e "Dumping the $SERVER_NAME ...\c"
for PID in $PIDS ; do
	jstack $PID > $DATE_DIR/jstack-$PID.dump 2>&1
	echo -e ".\c"
	jinfo $PID > $DATE_DIR/jinfo-$PID.dump 2>&1
	echo -e ".\c"
	jstat -gcutil $PID > $DATE_DIR/jstat-gcutil-$PID.dump 2>&1
	echo -e ".\c"
	jstat -gccapacity $PID > $DATE_DIR/jstat-gccapacity-$PID.dump 2>&1
	echo -e ".\c"
	jmap $PID > $DATE_DIR/jmap-$PID.dump 2>&1
	echo -e ".\c"
	jmap -heap $PID > $DATE_DIR/jmap-heap-$PID.dump 2>&1
	echo -e ".\c"
	jmap -histo $PID > $DATE_DIR/jmap-histo-$PID.dump 2>&1
	echo -e ".\c"
	if [ -r /usr/sbin/lsof ]; then
	/usr/sbin/lsof -p $PID > $DATE_DIR/lsof-$PID.dump
	echo -e ".\c"
	fi
done

if [ -r /bin/netstat ]; then
/bin/netstat -an > $DATE_DIR/netstat.dump 2>&1
echo -e ".\c"
fi
if [ -r /usr/bin/iostat ]; then
/usr/bin/iostat > $DATE_DIR/iostat.dump 2>&1
echo -e ".\c"
fi
if [ -r /usr/bin/mpstat ]; then
/usr/bin/mpstat > $DATE_DIR/mpstat.dump 2>&1
echo -e ".\c"
fi
if [ -r /usr/bin/vmstat ]; then
/usr/bin/vmstat > $DATE_DIR/vmstat.dump 2>&1
echo -e ".\c"
fi
if [ -r /usr/bin/free ]; then
/usr/bin/free -t > $DATE_DIR/free.dump 2>&1
echo -e ".\c"
fi
if [ -r /usr/bin/sar ]; then
/usr/bin/sar > $DATE_DIR/sar.dump 2>&1
echo -e ".\c"
fi
if [ -r /usr/bin/uptime ]; then
/usr/bin/uptime > $DATE_DIR/uptime.dump 2>&1
echo -e ".\c"
fi

echo "OK!"
echo "DUMP: $DATE_DIR"
