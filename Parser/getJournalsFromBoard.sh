#!/bin/bash
bbb_ip=192.168.1.158
output_dir=OutputJournals/
log_dir=/var/log/journal/
ssh_password=root

#TODO: change to get the list of /journal/ subdirectories sorted by modification date
echo SSH into BBB to get latest modified journal subdirectory
dir_to_copy=`sshpass -p $ssh_password ssh root@$bbb_ip ls $log_dir -At | head -n 1`

#TODO: Get the n latest modified journal files - Ignore the first instance of system.journal

echo Making output directory
mkdir $output_dir

#TODO: Iterate through the list of n latest modified journal files
echo Copying files from BBB into local system
sshpass -p $ssh_password scp -r root@$bbb_ip:$log_dir$dir_to_copy $output_dir
