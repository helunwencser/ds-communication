#!/bin/bash
slave=$(sed -n '4 p' ./dns)
ssh -i ~/.ssh/lwhecser.pem ubuntu@$slave
