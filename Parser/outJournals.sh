#!/bin/bash

for f in $(find ./ -name '*.journal'); do 
    journalctl --file $f > "$f.txt"
done

