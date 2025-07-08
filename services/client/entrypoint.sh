#!/bin/sh

# This script generates the config.json file from an environment variable.
# It uses a default value if PUBLIC_API_URL is not set.
echo "{\"PUBLIC_API_URL\":\"${PUBLIC_API_URL:-/api}\"}" > /usr/share/nginx/html/config.json

echo "--- Generated /usr/share/nginx/html/config.json ---"
cat /usr/share/nginx/html/config.json
echo "----------------------------------------------------"

# Start the Nginx web server
exec nginx -g 'daemon off;'
