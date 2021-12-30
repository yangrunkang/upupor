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
if [ "$1" = "start" ]; then
	./start.sh
else
	if [ "$1" = "stop" ]; then
		./stop.sh
	else
		if [ "$1" = "debug" ]; then
			./start.sh debug
		else
			if [ "$1" = "restart" ]; then
				./restart.sh
			else
				if [ "$1" = "dump" ]; then
					./dump.sh
				else
					echo "ERROR: Please input argument: start or stop or debug or restart or dump"
				    exit 1
				fi
			fi
		fi
	fi
fi
