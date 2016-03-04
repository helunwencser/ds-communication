#!/bin/bash
slave=$(sed -n '3 p' ./dns)
ssh -i ~/.ssh/lwhecser.pem ubuntu@$slave
