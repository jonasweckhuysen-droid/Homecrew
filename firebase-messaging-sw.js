importScripts("https://www.gstatic.com/firebasejs/10.12.2/firebase-app-compat.js");
importScripts("https://www.gstatic.com/firebasejs/10.12.2/firebase-messaging-compat.js");

firebase.initializeApp({
  apiKey: "AIzaSyCwlr069pxNXEEhIEGNug5Ly_kKSuvxSzs",
  authDomain: "homecrew-d8e21.firebaseapp.com",
  projectId: "homecrew-d8e21",
  messagingSenderId: "47664248188",
  appId: "1:47664248188:web:0994c6b2ba07f6fee060f9"
});

const messaging = firebase.messaging();

/* Push notificatie als app dicht is */
messaging.onBackgroundMessage((payload) => {
  self.registration.showNotification(payload.notification.title, {
    body: payload.notification.body,
    icon: "/Homecrew/icons/icon-192.png"
  });
});
