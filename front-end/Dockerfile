FROM node:18-alpine

WORKDIR /app

# Accept build arguments for React environment variables
ARG REACT_APP_API_URL
ARG REACT_APP_API_GATEWAY_URL

# Set environment variables for the build process
ENV REACT_APP_API_URL=$REACT_APP_API_URL
ENV REACT_APP_API_GATEWAY_URL=$REACT_APP_API_GATEWAY_URL

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application
COPY . .

# Build the application
RUN npm run build

# Install serve to run the built application
RUN npm install -g serve

EXPOSE 3000

# Serve the built application
CMD ["serve", "-s", "build", "-l", "3000"] 