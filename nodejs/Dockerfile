#
# Start by creating a base stage with env variable
# and some packages needed everywhere
#
FROM node:12.4-alpine as base
RUN apk add lz4-dev cyrus-sasl-dev openssl-dev
ENV SRC_LOCATION=/usr/local/nodejs-kafka-admin-example
ENV BUILD_LOCATION=/usr/local/build
ENV DEPENDENCIES_LOCATION=/usr/local/dependencies
ENV NODE_ENV=prod

#
# Create a new stage to download dependencies for build and realse stage
#
FROM base as dependencies
WORKDIR $DEPENDENCIES_LOCATION
RUN apk add bash g++ ca-certificates musl-dev make git
RUN apk add --virtual .build-deps gcc zlib-dev libc-dev bsd-compat-headers py-setuptools bash
COPY package.json package-lock.json $DEPENDENCIES_LOCATION/
RUN npm install
RUN cp -R $DEPENDENCIES_LOCATION/node_modules $DEPENDENCIES_LOCATION/dev/
RUN npm prune --production
RUN cp -R $DEPENDENCIES_LOCATION/node_modules $DEPENDENCIES_LOCATION/prod/

#
# Build stage, execute test and compile typescript to javascript
# create a realese directory with all needed to run application
#
FROM base as build
WORKDIR $BUILD_LOCATION
COPY --from=dependencies $DEPENDENCIES_LOCATION/dev $BUILD_LOCATION/node_modules
COPY . $BUILD_LOCATION
RUN node $BUILD_LOCATION/node_modules/.bin/tsc -p $BUILD_LOCATION/tsconfig.json
RUN mkdir $BUILD_LOCATION/release
RUN cp -R $BUILD_LOCATION/dist $BUILD_LOCATION/release/dist/
COPY --from=dependencies $DEPENDENCIES_LOCATION/prod $BUILD_LOCATION/release/node_modules
COPY package.json .env* open-api.yml* $BUILD_LOCATION/release/


#
# Realese stage, just set work directory,
# copy content of realese directory from build stage and set command
#
FROM base as release
WORKDIR $SRC_LOCATION
COPY --from=build $BUILD_LOCATION/release $SRC_LOCATION/
HEALTHCHECK --interval=12s --timeout=12s --start-period=30s CMD [ "npm", "run", "healthcheck" ]
CMD [ "npm", "start" ]
