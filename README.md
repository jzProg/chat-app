# chat-app

[![Build Status](https://travis-ci.org/jzProg/chat-app.svg?branch=master)](https://travis-ci.org/jzProg/chat-app/)

 A messenger app written in Vue js/Spring Boot. The real time communication and event broadcast are implemented using web sockets (https://github.com/sockjs/sockjs-client & https://github.com/stomp-js/stompjs). The aim of this project was to apply and be familiar with some web technologies and features (e.g push messaging, jwt authentication). Best practices were, also, applied by implementing object creation and validation using the Builder and Strategy Design patterns.

## Technologies and Tools

- **Webpack** (v4.41.2) / **Vue** (v2.5.2) for frontend.
- **Spring Boot** (v1.5.7), for backend.
- **liquibase** (v3.5.2), for initializing and managing the database schema on startup.

## App Features

  - User authentication and registration.
  - Create conversations and chat with other users (support for emoji, links etc).
  - Support for conversation deletion.
  - Update profile image by uploading.
  - Push notifications (use of **Push API** and service workers) to notify when a user logs in.

## Setup Instructions

- In the **/server/src/main/resources/application.properties** file, include the correct datasource info.
- cd to */server* folder.
- Build with **mvn clean install**.
- Run with **java -jar server-0.0.1-SNAPSHOT.jar** command.
- Access app on localhost:8080. 
- For *dev server*, run **npm run dev** from */web* folder. All requests from localhost:9090 are configured to be proxied to localhost:8080, in order to bypass CORS issues.

**Note**: To enable the Push notifications functionality, the **push.notifications.public.key**, **push.notifications.private.key** properties should be configured in the application.properties file (the keys can be produced through a VAPID key generator).
