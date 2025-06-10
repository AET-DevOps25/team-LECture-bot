# ---------- STAGE 1: Build ----------
FROM node:18-alpine AS builder

WORKDIR /client-app

COPY package*.json ./
RUN npm ci


# --- ADD THIS DEBUGGING LINE ---
RUN ls -l node_modules/axios || echo "axios not found"
# ---

COPY . .

RUN npm run build

# ---------- STAGE 2: Serve with Nginx ----------
FROM nginx:alpine

# Remove default nginx static assets
RUN rm -rf /usr/share/nginx/html/*

COPY --from=builder /client-app/dist /usr/share/nginx/html

COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 3000

CMD ["nginx", "-g", "daemon off;"]

