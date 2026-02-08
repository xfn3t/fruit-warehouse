FROM node:20-alpine AS deps

WORKDIR /app

COPY package.json package-lock.json ./

RUN --mount=type=cache,target=/root/.npm \
    npm ci --prefer-offline

FROM node:20-alpine AS builder

WORKDIR /app

COPY --from=deps /app/node_modules ./node_modules
COPY . .


FROM node:20-alpine

WORKDIR /app

RUN addgroup -g 1001 -S nodejs && \
    adduser -S nextjs -u 1001

COPY --from=builder --chown=nextjs:nodejs /app ./

USER nextjs

EXPOSE 4002

CMD ["npm", "run", "dev", "--", "-p", "4002"]

# CMD ["npm", "start"]