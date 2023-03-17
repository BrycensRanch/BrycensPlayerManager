#!/bin/bash

JV=`java -version 2>&1 >/dev/null | head -1`
echo $JV | sed -E 's/^.*version "([^".]*)\.[^"]*".*$/\1/'

if [ "$JV" != 17 ]; then
	case "$1" in
	install)
		echo "Installing SDKMAN..."
		curl -s "https://get.sdkman.io" | bash
		source ~/.sdkman/bin/sdkman-init.sh
		sdk version
		sdk install java 17.0.6-tem
		sdk use java 17.0.6-tem
		;;
	use)
		echo "must source ~/.sdkman/bin/sdkman-init.sh"
		exit 1
		;;
	esac
fi