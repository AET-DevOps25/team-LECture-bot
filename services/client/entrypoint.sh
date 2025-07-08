#!/bin/sh
# entrypoint.sh

# Set a default API URL for local development if not provided
export PUBLIC_API_URL=${PUBLIC_API_URL:-http://localhost:8080/api/v1/}

# Use 'envsubst' to replace the placeholder in the template with the real URL
envsubst '${PUBLIC_API_URL}' < /etc/nginx/templates/default.conf.template > /etc/nginx/conf.d/default.conf

echo "--- Nginx proxy configured to point to: ${PUBLIC_API_URL} ---"

# Start Nginx with the final configuration
exec nginx -g 'daemon off;'
