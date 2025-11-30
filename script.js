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
