'use strict';

self.addEventListener('push', function(event) {
  const title = event.data.json().title;
  const options = {
    body: event.data.json().message,
    icon: event.data.json().icon,
    badge: event.data.json().badge,
  };
  event.waitUntil(self.registration.showNotification(title, options));
});

self.addEventListener('notificationclick', function(event) {
  event.notification.close();
});
