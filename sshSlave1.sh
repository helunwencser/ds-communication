#!/bin/bash
slave=$(sed -n '2 p' ./dns)
ssh -i ~/.ssh/lwhecser.pem ubuntu@$slave
