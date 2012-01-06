#!/bin/sh

DATE=`date`

echo "**************************************************************************************"
echo "Starting RadarServer - $DATE"
echo "**************************************************************************************"

echo "Getting variables from env.txt"
. env.txt

if [ ! -d ${RCMBAKFOLDER} ]
then
	echo "**** Warning No backed up RadarServer at ${RCMBAKFOLDER} to copy config files from, exiting ****"
	exit 99
fi

echo "Backing up default installed drop-ins"
su - ${EDEXUSER} -c "mv ${RCMINSTALLFOLDER}/data/config/drop-ins ${RCMINSTALLFOLDER}/data/config/drop-ins-bak"
echo "Copying drop-ins from backup"
su - ${EDEXUSER} -c "cp -R ${RCMBAKFOLDER}/data/config/drop-ins ${RCMINSTALLFOLDER}/data/config/drop-ins"
echo "Backing up default installed persist folder"
su - ${EDEXUSER} -c "mv ${RCMINSTALLFOLDER}/data/config/persist ${RCMINSTALLFOLDER}/data/config/persist-bak"
echo "Copying persist folder from backup"
su - ${EDEXUSER} -c "cp -R ${RCMBAKFOLDER}/data/config/persist ${RCMINSTALLFOLDER}/data/config/persist"

echo "Starting RadarServer"
/etc/init.d/edex_rcm start

DATE=`date`

echo "--------------------------------------------------------------------------------------"
echo "Auto Radar Server Stop & Uninstall Completed At $DATE"
echo "--------------------------------------------------------------------------------------"
echo ""

exit
