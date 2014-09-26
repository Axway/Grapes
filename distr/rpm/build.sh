#!/bin/sh
if [ $# -ge 1 ]; then
	ARTIFACT_VERSION=$1
fi

if [ -z "$ARTIFACT_VERSION" ]; then
  ARTIFACT_VERSION=1.4.2
fi

DOWNLOADURL=https://repo1.maven.org/maven2/org/axway/grapes/grapes-server/$ARTIFACT_VERSION/grapes-server-$ARTIFACT_VERSION.jar

download_file_if_needed()
{
	URL=$1
	DEST=$2

	if [ ! -f $DEST ]; then

		echo "downloading from $URL to $DEST..."
		curl -L $URL -o $DEST

		case $DEST in
			*.tar.gz)
	        	tar tzf $DEST >>/dev/null 2>&1
	        	;;
	    	*.zip)
	        	unzip -t $DEST >>/dev/null 2>&1
	        	;;
	    	*.jar)
	        	unzip -t $DEST >>/dev/null 2>&1
	        	;;
	    	*.war)
	        	unzip -t $DEST >>/dev/null 2>&1
	        	;;
		esac

		if [ $? != 0 ]; then
			rm -f $DEST
			echo "invalid content for `basename $DEST` downloaded from $URL, discarding content and aborting build."
			exit -1
		fi

	fi
}

download_file_if_needed ${DOWNLOADURL} SOURCES/grapes-server-$ARTIFACT_VERSION.jar

# prepare fresh directories
rm -rf BUILD RPMS SRPMS TEMP
mkdir -p BUILD RPMS SRPMS TEMP

# Build using rpmbuild (use double-quote for define to have shell resolv vars !)
rpmbuild -bb --define="_topdir $PWD" --define="_tmppath $PWD/TEMP" --define="APP_VERSION ${ARTIFACT_VERSION}"  --define="APP_RELEASE $ARTIFACT_RPM_RELEASE" SPECS/grapes.spec

rpm -qpi --changelog RPMS/noarch/*.rpm
