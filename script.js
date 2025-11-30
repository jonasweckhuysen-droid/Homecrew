// ================= FIREBASE INIT =================
const firebaseConfig = {
  apiKey: "AIzaSyXXXX",
  authDomain: "homecrew-app.firebaseapp.com",
  projectId: "homecrew-app",
  storageBucket: "homecrew-app.appspot.com",
  messagingSenderId: "1234567890",
  appId: "1:1234567890:web:abcdef123456",
  measurementId: "G-XXXX"
};

firebase.initializeApp(firebaseConfig);
const db = firebase.firestore();
const users = {
    "jonas": "1234",
    "liese": "1234",
    "loreana": "1234"
};

// ================= LOGIN =================
function login() {
    const user = document.getElementById("username").value.trim().toLowerCase();
    const pass = document.getElementById("password").value.trim();

    if (users[user] && users[user] === pass) {
        localStorage.setItem("homecrewUser", user);
        window.location.href = "dashboard.html";
    } else {
        document.getElementById("error").textContent = "Ongeldige login";
    }
}

// ================= LOGOUT =================
function logout() {
    localStorage.removeItem("homecrewUser");
    window.location.href = "index.html";
}

// ================= DASHBOARD =================
if (window.location.pathname.includes("dashboard.html")) {
    const activeUser = localStorage.getItem("homecrewUser");
    if (!activeUser) window.location.href = "index.html";
    document.getElementById("welcome").innerText = "Welkom, " + activeUser + " 👋";
}

// ================= AGENDA =================
if (window.location.pathname.includes("agenda.html")) {
    const activeUser = localStorage.getItem("homecrewUser");
    if (!activeUser) window.location.href = "index.html";
    document.getElementById("welcome").innerText = "Welkom, " + activeUser + " 👋";
    loadEvents();
}

function addEvent() {
    const date = document.getElementById("date").value;
    const title = document.getElementById("title").value.trim();
    const description = document.getElementById("description").value.trim();
    const user = localStorage.getItem("homecrewUser");

    if (!date || !title) {
        alert("Datum en titel zijn verplicht");
        return;
    }

    const event = { date, title, description, user };

    let events = JSON.parse(localStorage.getItem("homecrewEvents")) || [];
    events.push(event);
    localStorage.setItem("homecrewEvents", JSON.stringify(events));

    document.getElementById("date").value = "";
    document.getElementById("title").value = "";
    document.getElementById("description").value = "";

    loadEvents();
}

function loadEvents() {
    const list = document.getElementById("events");
    list.innerHTML = "";

    let events = JSON.parse(localStorage.getItem("homecrewEvents")) || [];
    events.sort((a, b) => new Date(a.date) - new Date(b.date));

    events.forEach((event, index) => {
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
            <br><button onclick="deleteEvent(${index})">❌</button>
        `;

        list.appendChild(li);
    });
}

function deleteEvent(index) {
    let events = JSON.parse(localStorage.getItem("homecrewEvents")) || [];
    events.splice(index, 1);
    localStorage.setItem("homecrewEvents", JSON.stringify(events));
    loadEvents();
}

// ================= TAKEN =================
if (window.location.pathname.includes("taken.html")) {
    const activeUser = localStorage.getItem("homecrewUser");
    if (!activeUser) window.location.href = "index.html";
    document.getElementById("welcome").innerText = "Welkom, " + activeUser + " 👋";
    loadTasks();
}

function addTask() {
    const title = document.getElementById("taskTitle").value.trim();
    const desc = document.getElementById("taskDesc").value.trim();
    const user = localStorage.getItem("homecrewUser");

    if (!title) {
        alert("Titel is verplicht");
        return;
    }

    const task = { title, desc, user, done: false };

    let tasks = JSON.parse(localStorage.getItem("homecrewTasks")) || [];
    tasks.push(task);
    localStorage.setItem("homecrewTasks", JSON.stringify(tasks));

    document.getElementById("taskTitle").value = "";
    document.getElementById("taskDesc").value = "";

    loadTasks();
}

function loadTasks() {
    const list = document.getElementById("tasks");
    list.innerHTML = "";

    let tasks = JSON.parse(localStorage.getItem("homecrewTasks")) || [];

    tasks.forEach((task, index) => {
        const li = document.createElement("li");

        let userClass = "";
        if(task.user === "jonas") userClass = "user-jonas";
        else if(task.user === "liese") userClass = "user-liese";
        else if(task.user === "loreana") userClass = "user-loreana";

        li.className = userClass;

        li.innerHTML = `
            <input type="checkbox" ${task.done ? "checked" : ""} onclick="toggleTask(${index})">
            <strong>${task.title}</strong> (${task.user})<br>
            ${task.desc || ""}
            <br><button onclick="deleteTask(${index})">❌</button>
        `;

        list.appendChild(li);
    });
}

function toggleTask(index) {
    let tasks = JSON.parse(localStorage.getItem("homecrewTasks")) || [];
    tasks[index].done = !tasks[index].done;
    localStorage.setItem("homecrewTasks", JSON.stringify(tasks));
    loadTasks();
}

function deleteTask(index) {
    let tasks = JSON.parse(localStorage.getItem("homecrewTasks")) || [];
    tasks.splice(index, 1);
    localStorage.setItem("homecrewTasks", JSON.stringify(tasks));
    loadTasks();
}
/* Algemene body */
body {
    margin: 0;
    padding: 0;
    font-family: 'Arial', sans-serif;
    background: linear-gradient(135deg, #f3f1ec, #e0f7fa);
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
}

/* Dashboard container */
.dashboard-container {
    text-align: center;
    max-width: 500px;
    padding: 20px;
}

/* Welcome banner */
.welcome-banner {
    background: linear-gradient(90deg, #ff9a9e, #fad0c4, #a1c4fd);
    padding: 25px;
    border-radius: 20px;
    box-shadow: 0 6px 15px rgba(0,0,0,0.2);
    margin-bottom: 30px;
    animation: fadeInDown 1s ease-out;
    color: white;
}

.welcome-banner h1 {
    margin: 0;
    font-size: 28px;
}

.welcome-banner p {
    margin-top: 8px;
    font-size: 16px;
}

/* Dashboard knoppen */
.dashboard-buttons {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.dash-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    padding: 18px;
    border-radius: 15px;
    font-size: 20px;
    color: white;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 6px 12px rgba(0,0,0,0.2);
}

.dash-btn span {
    font-weight: bold;
}

/* Kleuren en iconen per knop */
.dash-btn.agenda { background: #3a6ea5; }
.dash-btn.taken { background: #ff6f61; }

.dash-btn:hover {
    transform: translateY(-5px) scale(1.02);
    box-shadow: 0 10px 20px rgba(0,0,0,0.3);
}

/* Logout knop */
.logout-btn {
    margin-top: 30px;
    width: 100%;
    padding: 15px;
    border-radius: 15px;
    border: none;
    font-size: 16px;
    background: #dc3545;
    color: white;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    transition: all 0.2s;
}

.logout-btn:hover {
    background: #c82333;
    transform: translateY(-3px);
}

/* Animaties */
@keyframes fadeInDown {
    from { opacity: 0; transform: translateY(-30px); }
    to { opacity: 1; transform: translateY(0); }
}
