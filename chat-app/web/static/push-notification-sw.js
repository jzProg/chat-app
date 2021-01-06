'use strict';
importScripts("localforage.min.js");

self.addEventListener('push', function(event) {
  const { title, message, icon, badge, convId } = event.data.json();
  const options = {
    body: message,
    icon,
    badge,
  };
  event.waitUntil(
    localforage.getItem('activeConv', function (err, value) {
      if (!convId || !value || parseInt(value) !== parseInt(convId, 10))
        return self.registration.showNotification(title, options);
    })
  );
});

self.addEventListener('notificationclick', function(event) {
  event.notification.close();
});
