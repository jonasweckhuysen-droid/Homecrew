import { initializeApp } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-app.js";
import { 
  getDatabase, 
  ref, 
  push, 
  onChildAdded, 
  set 
} from "https://www.gstatic.com/firebasejs/10.12.2/firebase-database.js";

import { 
  getMessaging, 
  getToken 
} from "https://www.gstatic.com/firebasejs/10.12.2/firebase-messaging.js";

/* === FIREBASE CONFIG === */
const firebaseConfig = {
  apiKey: "AIzaSyCwlr069pxNXEEhIEGNug5Ly_kKSuvxSzs",
  authDomain: "homecrew-d8e21.firebaseapp.com",
  databaseURL: "https://homecrew-d8e21-default-rtdb.europe-west1.firebasedatabase.app",
  projectId: "homecrew-d8e21",
  storageBucket: "homecrew-d8e21.appspot.com",
  messagingSenderId: "47664248188",
  appId: "1:47664248188:web:0994c6b2ba07f6fee060f9"
};

/* === INIT === */
const app = initializeApp(firebaseConfig);
const database = getDatabase(app);
const messaging = getMessaging(app);

const messagesRef = ref(database, "messages");
const tokensRef = ref(database, "tokens");

/* === USER === */
const user = localStorage.getItem("homecrewUser") || "Onbekend";

/* === ELEMENTS === */
const chatBox = document.getElementById("chatBox");
const messageInput = document.getElementById("messageInput");

/* === VAPID KEY === */
const vapidKey = "BK-eK6EIYGINc5yLv1k7wwAjGARjlI25X10EFht7XhXtMaUfq2gCIaY7U52pmmzOwsdTHIqGz7z5M8b8dOnMPJo";

/* === NOTIFICATION PERMISSION + TOKEN OPSLAAN === */
Notification.requestPermission().then(permission => {
  if (permission === "granted") {
    getToken(messaging, { vapidKey: vapidKey })
      .then(token => {
        if (token) {
          console.log("✅ Token ontvangen:", token);

          // Opslaan in firebase
          const userTokenRef = ref(database, "tokens/" + user.replace(/\s/g, "_"));
          set(userTokenRef, token);
        }
      })
      .catch(error => {
        console.error("❌ Token fout:", error);
      });
  }
});

/* === BERICHT VERZENDEN === */
window.sendMessage = function () {
  const text = messageInput.value.trim();
  if (!text) return;

  push(messagesRef, {
    user: user,
    text: text,
    timestamp: Date.now(),
  });

  messageInput.value = "";
};

/* === BERICHTEN ONTVANGEN === */
onChildAdded(messagesRef, (snapshot) => {
  const msg = snapshot.val();

  const messageDiv = document.createElement("div");
  messageDiv.className = msg.user === user ? "my-message" : "other-message";

  const time = new Date(msg.timestamp).toLocaleTimeString("nl-BE", {
    hour: '2-digit',
    minute: '2-digit'
  });

  messageDiv.innerHTML = `
    <b>${msg.user}</b>
    <p>${msg.text}</p>
    <small>${time}</small>
  `;

  chatBox.appendChild(messageDiv);
  chatBox.scrollTop = chatBox.scrollHeight;

  if (msg.user !== user) {
    showNotification(msg.user, msg.text);
  }
});

/* === LOCALE NOTIFICATIE (wanneer app open is) === */
function showNotification(sender, text) {
  if (Notification.permission === "granted") {
    navigator.serviceWorker.ready.then(reg => {
      reg.showNotification("Nieuw bericht van " + sender, {
        body: text,
        icon: "/Homecrew/icons/icon-192.png",
        badge: "/Homecrew/icons/icon-192.png"
      });
    });
  }
}
