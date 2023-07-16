#!/bin/bash

set -e

# Run original entrypoint script
/usr/local/bin/jenkins.sh &

# Wait for Jenkins to start
until [[ -f /var/jenkins_home/secrets/initialAdminPassword ]]; do
  sleep 1
done

# Expose Jenkins master password
cat /var/jenkins_home/secrets/initialAdminPassword > /var/jenkins_home/exposedAdminPassword

# Keep the container running
tail -f /dev/null
