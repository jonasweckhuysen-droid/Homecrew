import { initializeApp } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-app.js";
import { getDatabase, ref, push, onChildAdded } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-database.js";
import { getMessaging, getToken } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-messaging.js";

/* JOUW FIREBASE CONFIG */
const firebaseConfig = {
  apiKey: "AIzaSyCwlr069pxNXEEhIEGNug5Ly_kKSuvxSzs",
  authDomain: "homecrew-d8e21.firebaseapp.com",
  databaseURL: "https://homecrew-d8e21-default-rtdb.europe-west1.firebasedatabase.app",
  projectId: "homecrew-d8e21",
  storageBucket: "homecrew-d8e21.appspot.com",
  messagingSenderId: "47664248188",
  appId: "1:47664248188:web:0994c6b2ba07f6fee060f9",
  measurementId: "G-5R58JL5D5K"
};

const app = initializeApp(firebaseConfig);
const database = getDatabase(app);
const messagesRef = ref(database, 'messages');
const messaging = getMessaging(app);

const user = localStorage.getItem("homecrewUser") || "Onbekend";
const chatBox = document.getElementById("chatBox");
const messageInput = document.getElementById("messageInput");

/* === VAPID KEY HIER PLAKKEN === */
const vapidKey = "BK-eK6EIYGINc5yLv1k7wwAjGARjlI25X10EFht7XhXtMaUfq2gCIaY7U52pmmzOwsdTHIqGz7z5M8b8dOnMPJo";

/* Push notificaties toestaan */
Notification.requestPermission().then(permission => {
    if (permission === "granted") {
        getToken(messaging, { vapidKey: vapidKey })
            .then((currentToken) => {
                if (currentToken) {
                    console.log("Push token:", currentToken);
                }
            })
            .catch((err) => {
                console.log("Token error:", err);
            });
    }
});

/* Bericht verzenden */
window.sendMessage = function() {
    const text = messageInput.value.trim();
    if (!text) return;

    push(messagesRef, {
        user: user,
        text: text,
        time: new Date().toLocaleTimeString()
    });

    messageInput.value = "";
}

/* Berichten live tonen */
onChildAdded(messagesRef, function(snapshot) {
    const msg = snapshot.val();

    const messageDiv = document.createElement("div");
    messageDiv.className = msg.user === user ? "my-message" : "other-message";

    messageDiv.innerHTML = `
        <b>${msg.user}</b>
        <p>${msg.text}</p>
        <small>${msg.time}</small>
    `;

    chatBox.appendChild(messageDiv);
    chatBox.scrollTop = chatBox.scrollHeight;

    if (msg.user !== user) {
        showNotification(msg.user, msg.text);
    }
});

/* Push melding tonen */
function showNotification(sender, text) {
    if (Notification.permission === "granted") {
        navigator.serviceWorker.ready.then(reg => {
            reg.showNotification("Nieuw bericht van " + sender, {
                body: text,
                icon: "/Homecrew/icons/icon-192.png"
            });
        });
    }
                  }
