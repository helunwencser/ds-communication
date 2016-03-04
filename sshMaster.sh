#!/bin/bash
master=$(sed -n '1 p' ./dns)
ssh -i ~/.ssh/lwhecser.pem ubuntu@$master
