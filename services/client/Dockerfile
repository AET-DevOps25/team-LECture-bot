# ---------- STAGE 1: Build ----------
FROM node:18-alpine AS builder

WORKDIR /client-app

COPY package*.json ./
RUN npm ci

COPY . .

RUN npm run build

# ---------- STAGE 2: Serve with Nginx ----------
FROM nginx:alpine

# Install 'gettext' to get the 'envsubst' command
RUN apk add --no-cache gettext

# Remove default nginx static assets
RUN rm -rf /usr/share/nginx/html/*

COPY --from=builder /client-app/dist /usr/share/nginx/html

COPY nginx.conf.template /etc/nginx/templates/default.conf.template

COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 3000

ENTRYPOINT ["/entrypoint.sh"] 

