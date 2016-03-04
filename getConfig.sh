#!/bin/bash
master=$(head -n 1 ./dns)
while true;
do
    scp -i lwhecser.pem ubuntu@$master:/home/ubuntu/ds-lab0/node_conf.yaml .
    sleep 1
done
