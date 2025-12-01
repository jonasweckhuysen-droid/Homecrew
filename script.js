/*************************
  HOME CREW - SCRIPT.JS
**************************/

// =====================
// GEBRUIKER KLEUR
// =====================
function getUserColor(user) {
    const colors = {
        jonas: "#3498db",
        liese: "#e74c3c",
        loreana: "#ff69b4"
    };
    return colors[user.toLowerCase()] || "#555";
}

// =====================
// CHECK LOGIN STATUS
// =====================
document.addEventListener("DOMContentLoaded", () => {
    const page = window.location.pathname.split("/").pop();

    const logged =
        localStorage.getItem("homecrew_loggedin") === "true" ||
        sessionStorage.getItem("homecrew_loggedin") === "true";

    // Indexpagina → al ingelogd? dan dashboard
    if ((page === "" || page === "index.html") && logged) {
        window.location.replace("dashboard.html");
    }

    // Niet-indexpagina → niet ingelogd? dan index
    if ((page !== "" && page !== "index.html") && !logged) {
        window.location.replace("index.html");
    }

    // Taken automatisch laden als lijst aanwezig
    if (document.getElementById("tasks")) {
        loadTasks();
    }
});

// =====================
// LOGIN / REGISTRATIE
// =====================
function login() {
    const name = document.getElementById("username").value.trim();
    const pass = document.getElementById("password").value.trim();
    const remember = document.getElementById("stayLoggedIn")?.checked;

    if (!name || !pass) {
        alert("Vul naam en wachtwoord/pincode in");
        return;
    }

    // Nieuwe of bestaande gebruiker
    const users = JSON.parse(localStorage.getItem("homecrewUsers") || "{}");

    if (users[name]) {
        // Bestaande gebruiker → check wachtwoord
        if (users[name] !== pass) {
            alert("Onjuist wachtwoord!");
            return;
        }
    } else {
        // Nieuwe gebruiker → account aanmaken
        users[name] = pass;
        localStorage.setItem("homecrewUsers", JSON.stringify(users));
    }

    // Opslaan ingelogde status
    if (remember) {
        localStorage.setItem("homecrew_loggedin", "true");
        localStorage.setItem("homecrewUser", name);
    } else {
        sessionStorage.setItem("homecrew_loggedin", "true");
        sessionStorage.setItem("homecrewUser", name);
    }

    window.location.href = "dashboard.html";
}

// =====================
// LOGOUT
// =====================
function logout() {
    localStorage.removeItem("homecrew_loggedin");
    localStorage.removeItem("homecrewUser");
    sessionStorage.removeItem("homecrew_loggedin");
    sessionStorage.removeItem("homecrewUser");
    window.location.href = "index.html";
}

// =====================
// TAKEN (GEMEENSCHAPPELIJK)
// =====================
function loadTasks() {
    const tasks = JSON.parse(localStorage.getItem("homecrew_tasks")) || [];
    const ul = document.getElementById("tasks");
    if (!ul) return;

    ul.innerHTML = "";

    tasks.forEach((task, index) => {
        const color = getUserColor(task.user);

        const li = document.createElement("li");
        li.style.borderLeft = `6px solid ${color}`;
        li.style.padding = "10px";
        li.style.marginBottom = "12px";
        li.style.borderRadius = "12px";
        li.style.background = "#f9f9f9";
        li.style.listStyle = "none";

        li.innerHTML = `
            <span class="user-dot" style="background:${color}"></span>
            <strong>${task.title}</strong>
            ${task.desc ? `<p>${task.desc}</p>` : ""}
            <small style="color:${color}">👤 ${task.user}</small>
            <button class="delete-btn">❌</button>
        `;

        li.querySelector(".delete-btn").onclick = () => removeTask(index);
        ul.appendChild(li);
    });
}

function addTask() {
    const title = document.getElementById("taskTitle")?.value.trim();
    const desc = document.getElementById("taskDesc")?.value.trim();
    const activeUser =
        localStorage.getItem("homecrewUser") || sessionStorage.getItem("homecrewUser");

    if (!title) {
        alert("Vul een titel in");
        return;
    }

    if (!activeUser) {
        alert("Geen gebruiker gevonden, log opnieuw in");
        return;
    }

    const tasks = JSON.parse(localStorage.getItem("homecrew_tasks")) || [];
    tasks.push({ title, desc, user: activeUser, created: Date.now() });
    localStorage.setItem("homecrew_tasks", JSON.stringify(tasks));

    if (document.getElementById("taskTitle")) document.getElementById("taskTitle").value = "";
    if (document.getElementById("taskDesc")) document.getElementById("taskDesc").value = "";
    loadTasks();
}

function removeTask(index) {
    const tasks = JSON.parse(localStorage.getItem("homecrew_tasks")) || [];
    tasks.splice(index, 1);
    localStorage.setItem("homecrew_tasks", JSON.stringify(tasks));
    loadTasks();
}

// =====================
// Exporteer functies
// =====================
window.login = login;
window.logout = logout;
window.addTask = addTask;
window.loadTasks = loadTasks;
