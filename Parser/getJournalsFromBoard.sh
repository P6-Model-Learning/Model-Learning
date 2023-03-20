#!/bin/bash
bbb_ip=192.168.1.158
output_dir=OutputJournals/
log_dir=/var/log/journal/
ssh_password=root
ignore_system_journal=true
declare -i files_copied=0

#Get amount of files to copy from first positional parameter - Set limit to 100 if no parameter is provided
declare -i amount_of_files=${1:-100}

echo Making output directory
mkdir $output_dir

echo SSH into BBB to get latest modified journal subdirectory
sorted_journal_sub_directories=`sshpass -p $ssh_password ssh root@$bbb_ip ls $log_dir -At`

for sub_dir in $sorted_journal_sub_directories; do
    if (( $files_copied >= $amount_of_files )); then
        echo File limit of $amount_of_files reached - Outer loop
        break
    fi

    output_sub_dir=$output_dir$sub_dir/
    echo Making log subdirectory in output directory
    mkdir $output_sub_dir

    full_path_cur_sub_dir=$log_dir$sub_dir/
    sorted_journal_in_cur_dir=`sshpass -p $ssh_password ssh root@$bbb_ip ls $full_path_cur_sub_dir -At`

    for journal in $sorted_journal_in_cur_dir; do
        if (( $files_copied >= $amount_of_files )); then
            echo File limit of $amount_of_files reached - Inner loop
            break
        fi

        #Ignore latest instance of system.journal 
        if $ignore_system_journal && [[ "$journal" == "system.journal" ]]; then
            echo Latest instance of system.journal skipped
            ignore_system_journal=false
            continue
        fi

        echo Copying $journal from directory $full_path_cur_sub_dir in BBB
        sshpass -p $ssh_password scp root@$bbb_ip:$full_path_cur_sub_dir$journal $output_sub_dir
        files_copied=$(( files_copied + 1 ))
    done
done