/*************************
 HOMECREW - SCRIPT.JS
**************************/

// =====================
// LOGIN CHECK
// =====================
function isLoggedIn() {
    return !!(localStorage.getItem("homecrewUser") || sessionStorage.getItem("homecrewUser"));
}

function getActiveUser() {
    return localStorage.getItem("homecrewUser") || sessionStorage.getItem("homecrewUser");
}

function login(username, password, stayLoggedIn) {
    if (!username || !password) {
        document.getElementById("error").innerText = "Vul naam en wachtwoord/pincode in";
        return;
    }

    const users = JSON.parse(localStorage.getItem("homecrewUsers") || "{}");

    // Bestaat al maar wachtwoord fout
    if (users[username] && users[username] !== password) {
        document.getElementById("error").innerText = "Onjuist wachtwoord!";
        return;
    }

    // Nieuwe gebruiker registreren
    users[username] = password;
    localStorage.setItem("homecrewUsers", JSON.stringify(users));

    if (stayLoggedIn) {
        localStorage.setItem("homecrewUser", username);
    } else {
        sessionStorage.setItem("homecrewUser", username);
    }

    window.location.href = "dashboard.html";
}

function logout() {
    localStorage.removeItem("homecrewUser");
    sessionStorage.removeItem("homecrewUser");
    window.location.href = "index.html";
}

// =====================
// TAKEN FUNCTIES
// =====================
function loadTasks() {
    const tasks = JSON.parse(localStorage.getItem("tasks_all") || "[]");
    const ul = document.getElementById("tasks");
    if (!ul) return;

    ul.innerHTML = "";
    const user = getActiveUser();

    tasks.forEach((task, index) => {
        const li = document.createElement("li");

        li.innerHTML = `
            <strong>${task.title}</strong>
            <p>${task.desc}</p>
            <small>${task.user || ""}</small>
            <button class="delete-btn">❌</button>
        `;

        li.querySelector(".delete-btn").onclick = () => removeTask(index);
        ul.appendChild(li);
    });
}

function addTask() {
    const title = document.getElementById("taskTitle")?.value.trim();
    const desc = document.getElementById("taskDesc")?.value.trim();
    if (!title) return alert("Vul een titel in");

    const user = getActiveUser();
    const tasks = JSON.parse(localStorage.getItem("tasks_all") || "[]");

    tasks.push({ title, desc, user });

    localStorage.setItem("tasks_all", JSON.stringify(tasks));

    if (document.getElementById("taskTitle")) document.getElementById("taskTitle").value = "";
    if (document.getElementById("taskDesc")) document.getElementById("taskDesc").value = "";

    loadTasks();
}

function removeTask(index) {
    const tasks = JSON.parse(localStorage.getItem("tasks_all") || "[]");
    tasks.splice(index, 1);
    localStorage.setItem("tasks_all", JSON.stringify(tasks));
    loadTasks();
}

// =====================
// EVENT LISTENERS
// =====================
document.addEventListener("DOMContentLoaded", () => {

    // AUTO-LOGIN
    if (document.getElementById("username") && isLoggedIn()) {
        window.location.href = "dashboard.html";
        return;
    }

    // LOGIN KNOP
    const startBtn = document.getElementById("startBtn");
    if (startBtn) {
        startBtn.addEventListener("click", () => {
            const username = document.getElementById("username").value.trim();
            const password = document.getElementById("password").value.trim();
            const stay = document.getElementById("stayLoggedIn").checked;

            login(username, password, stay);
        });
    }

    // DASHBOARD WELCOME
    const welcomeEl = document.getElementById("welcome");
    const user = getActiveUser();
    if (welcomeEl && user) {
        welcomeEl.innerText = "Welkom, " + user + " 👋";
    }

    // TAKEN
    const addBtn = document.getElementById("addTaskBtn");
    if (addBtn) addBtn.onclick = addTask;

    const logoutBtn = document.getElementById("logoutBtn");
    if (logoutBtn) logoutBtn.onclick = logout;

    if (document.getElementById("tasks")) {
        loadTasks();
    }

    // Navigatie
    const btnTaken = document.getElementById("btnTaken");
    if (btnTaken) btnTaken.onclick = () => window.location.href = "taken.html";

});
