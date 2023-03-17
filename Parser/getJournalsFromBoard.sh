#!/bin/bash
output_dir=OutputJournals/
bbb_ip=192.168.1.158
log_dir=/var/log/journal/
dir_to_copy=`ssh root@$bbb_ip ls $log_dir -At | head -n 1`

mkdir $output_dir

#copy from BBB to local system
scp -r root@$bbb_ip:$log_dir$dir_to_copy $output_dir
