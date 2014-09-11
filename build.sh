#!/bin/sh
# This is an example build of  the test to build  teamengine and the WPS test for UNIX/OS

# change to true if teamengine is not installed in your system.
build_teamengine=false

# TEAM Engine tag from https://github.com/opengeospatial/teamengine
te_tag=4.0.5

# base to download and install teamengine. 
# change the base if the directory is other than the current user default directory

base=~
mkdir -p $base/teamengine/TE_BASE/scripts
mkdir -p $base/teamengine/te_build
mkdir -p $base/teamengine/te_install 
folder_to_build=$base/teamengine/te_build
te_install=$base/teamengine/te_install

current_dir_test=`pwd`

# where the ETS tests scripts exists
TE_BASE=$base/teamengine/TE_BASE

if build_teamengine; then
   #download and build TEAM Engine
   cd $folder_to_build
   git clone https://github.com/opengeospatial/teamengine.git
   cd $base/teamengine/te_build/teamengine
   git checkout $te_tag
   mvn install
   unzip $folder_to_build/teamengine/teamengine-console/target/teamengine-console-$te_tag-bin.zip -d $te_install
   unzip $folder_to_build/teamengine/teamengine-console/target/teamengine-console-$te_tag-base.zip -d $TE_BASE
fi   


########
# build the test

cd $current_dir_test
mvn install
cd target 

# will get the zip file containing the scripts
ls *ctl.zip > temp_out
zip_ctl_file=`grep ctl temp_out | grep zip | head -1`


#  install the test scripts
unzip -q -o $zip_ctl_file -d $TE_BASE/scripts

echo "  "
echo "-----------------   "
echo "  "
echo "Congratulations"
echo "  "
echo "TEAM Engine ($te_tag) and the ets-wps10-1.0.0 test have been installed."
echo "  "
echo " UNIX/OS users - You can run the test with the following commands: "
echo "   export TE_BASE=$TE_BASE"
echo "   $base/teamengine/te_install/bin/unix/test.sh -source=wps/1.0.0/ctl/wps.xml"
echo "  "
echo " Windows users - You can run the test with the following commands:"
echo "   set TE_BASE=$TE_BASE"
echo "   $base/teamengine/te_install/bin/windows/test.bat -source=wps/1.0.0/ctl/wps.xml"
echo "  "
echo " Any issue or questions please send an email to the CITE forum: 
http://cite.opengeospatial.org/forum"
echo "  "
