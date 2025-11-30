// ================= FIREBASE IMPORT =================
import { initializeApp } from "https://www.gstatic.com/firebasejs/10.8.1/firebase-app.js";
import { getFirestore, collection, addDoc, onSnapshot, deleteDoc, doc, updateDoc, query, orderBy } from "https://www.gstatic.com/firebasejs/10.8.1/firebase-firestore.js";

// ================= FIREBASE CONFIG =================
const firebaseConfig = {
  apiKey: "AIzaSyCwlr069pxNXEEhIEGNug5Ly_kKSuvxSzs",
  authDomain: "homecrew-d8e21.firebaseapp.com",
  projectId: "homecrew-d8e21",
  storageBucket: "homecrew-d8e21.firebasestorage.app",
  messagingSenderId: "47664248188",
  appId: "1:47664248188:web:0994c6b2ba07f6fee060f9",
  measurementId: "G-5R58JL5D5K"
};

// ================= INIT =================
const app = initializeApp(firebaseConfig);
const db = getFirestore(app);

// ================= LOGIN (blijft lokaal) =================
const users = {
    "jonas": "1234",
    "liese": "1234",
    "loreana": "1234"
};

window.login = function() {
    const user = document.getElementById("username").value.trim().toLowerCase();
    const pass = document.getElementById("password").value.trim();

    if (users[user] && users[user] === pass) {
        localStorage.setItem("homecrewUser", user);
        window.location.href = "dashboard.html";
    } else {
        document.getElementById("error").textContent = "Ongeldige login";
    }
};

window.logout = function() {
    localStorage.removeItem("homecrewUser");
    window.location.href = "index.html";
};

// ================= GEBRUIKER CONTROLE =================
const activeUser = localStorage.getItem("homecrewUser");

if (document.getElementById("welcome") && activeUser) {
    document.getElementById("welcome").textContent = "Welkom, " + activeUser + " 👋";
}

// ================= AGENDA =================

if (window.location.pathname.includes("agenda.html")) {
    if (!activeUser) window.location.href = "index.html";
    loadEvents();
}

window.addEvent = async function() {
    const date = document.getElementById("date").value;
    const title = document.getElementById("title").value.trim();
    const description = document.getElementById("description").value.trim();

    if (!date || !title) {
        alert("Datum en titel zijn verplicht");
        return;
    }

    await addDoc(collection(db, "events"), {
        date: date,
        title: title,
        description: description,
        user: activeUser,
        created: new Date()
    });

    document.getElementById("date").value = "";
    document.getElementById("title").value = "";
    document.getElementById("description").value = "";
}

function loadEvents() {
    const list = document.getElementById("events");

    const q = query(collection(db, "events"), orderBy("date"));

    onSnapshot(q, (snapshot) => {
        list.innerHTML = "";

        snapshot.forEach((docSnapshot) => {
            const event = docSnapshot.data();
            const id = docSnapshot.id;

            const li = document.createElement("li");

            let userClass = "";
            if(event.user === "jonas") userClass = "user-jonas";
            else if(event.user === "liese") userClass = "user-liese";
            else if(event.user === "loreana") userClass = "user-loreana";

            li.className = userClass;
            li.innerHTML = `
                <strong>${event.date}</strong><br>
                ${event.title}<br>
                <small>Door: ${event.user}</small><br>
                ${event.description || ""}
                <br>
                <button onclick="deleteEvent('${id}')">❌</button>
            `;
            list.appendChild(li);
        });
    });
}

window.deleteEvent = async function(id) {
    await deleteDoc(doc(db, "events", id));
};

// ================= TAKEN =================

if (window.location.pathname.includes("taken.html")) {
    if (!activeUser) window.location.href = "index.html";
    loadTasks();
}

window.addTask = async function() {
    const title = document.getElementById("taskTitle").value.trim();
    const desc = document.getElementById("taskDesc").value.trim();

    if (!title) {
        alert("Titel is verplicht");
        return;
    }

    await addDoc(collection(db, "tasks"), {
        title: title,
        desc: desc,
        user: activeUser,
        done: false,
        created: new Date()
    });

    document.getElementById("taskTitle").value = "";
    document.getElementById("taskDesc").value = "";
}

function loadTasks() {
    const list = document.getElementById("tasks");
    const q = query(collection(db, "tasks"), orderBy("created"));

    onSnapshot(q, (snapshot) => {
        list.innerHTML = "";

        snapshot.forEach((docSnapshot) => {
            const task = docSnapshot.data();
            const id = docSnapshot.id;

            const li = document.createElement("li");

            let userClass = "";
            if(task.user === "jonas") userClass = "user-jonas";
            else if(task.user === "liese") userClass = "user-liese";
            else if(task.user === "loreana") userClass = "user-loreana";

            li.className = userClass;

            li.innerHTML = `
                <input type="checkbox" ${task.done ? "checked" : ""} onclick="toggleTask('${id}', ${task.done})">
                <strong>${task.title}</strong> (${task.user})<br>
                ${task.desc || ""}
                <br>
                <button onclick="deleteTask('${id}')">❌</button>
            `;

            list.appendChild(li);
        });
    });
}

window.toggleTask = async function(id, status) {
    await updateDoc(doc(db, "tasks", id), {
        done: !status
    });
};

window.deleteTask = async function(id) {
    await deleteDoc(doc(db, "tasks", id));
};

import { getAuth, setPersistence, browserLocalPersistence, onAuthStateChanged, signInWithEmailAndPassword } 
from "https://www.gstatic.com/firebasejs/10.12.2/firebase-auth.js";

const auth = getAuth();

// ZORG DAT DE USER BLIJFT INGELOGD
setPersistence(auth, browserLocalPersistence)
.then(() => {
  console.log("Gebruiker wordt onthouden op dit toestel");
})
.catch((error) => {
  console.error("Fout bij instellen persistentie:", error);
});
onAuthStateChanged(auth, (user) => {
  if (user) {
    console.log("Automatisch ingelogd:", user.email);
    window.location.href = "dashboard.html"; // of je startpagina naar keuze
  } else {
    console.log("Niet ingelogd");
  }
});
