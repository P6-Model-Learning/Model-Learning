#!/bin/bash
bbb_ip=192.168.1.158
output_dir=OutputJournals/
log_dir=/var/log/journal/
ssh_password=root

#Get latest modified subdirectory of journal
dir_to_copy=`sshpass -p $ssh_password ssh root@$bbb_ip ls $log_dir -At | head -n 1`

mkdir $output_dir

#copy from BBB to local system
sshpass -p $ssh_password scp -r root@$bbb_ip:$log_dir$dir_to_copy $output_dir
