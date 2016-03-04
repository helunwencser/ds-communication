#!/bin/bash
master=$(sed -n '1 p' ./dns)
scp -i ~/.ssh/lwhecser.pem -r ../ds-lab3 ubuntu@$master:~/
slave1=$(sed -n '2 p' ./dns)
scp -i ~/.ssh/lwhecser.pem -r ../ds-lab3 ubuntu@$slave1:~/
slave2=$(sed -n '3 p' ./dns)
scp -i ~/.ssh/lwhecser.pem -r ../ds-lab3 ubuntu@$slave2:~/
slave3=$(sed -n '4 p' ./dns)
scp -i ~/.ssh/lwhecser.pem -r ../ds-lab3 ubuntu@$slave3:~/
