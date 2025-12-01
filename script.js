/*************************
  HOME CREW - SCRIPT.JS
**************************/

// =====================
// GEBRUIKER KLEUR
// =====================
function getUserColor(user) {
    if (user === "jonas") return "#3498db";   // blauw
    if (user === "liese") return "#e74c3c";   // rood
    if (user === "loreana") return "#ff69b4"; // roze
    return "#555"; // standaard
}

// =====================
// CHECK LOGIN STATUS
// =====================
document.addEventListener("DOMContentLoaded", () => {

    const page = window.location.pathname.split("/").pop();

    const logged =
        localStorage.getItem("homecrew_loggedin") === "true" ||
        sessionStorage.getItem("homecrew_loggedin") === "true";

    // Als je al ingelogd bent en op index zit → naar dashboard
    if ((page === "" || page === "index.html") && logged) {
        window.location.replace("dashboard.html");
    }

    // Als je NIET bent ingelogd en niet op index zit → terug naar index
    if ((page !== "" && page !== "index.html") && !logged) {
        window.location.replace("index.html");
    }

    // Taken automatisch laden als die lijst bestaat
    if (document.getElementById("tasks")) {
        loadTasks();
    }

});


// =====================
// LOGIN
// =====================
function login() {

    const name = document.getElementById("username").value.trim().toLowerCase();
    const pass = document.getElementById("password").value.trim();
    const remember = document.getElementById("rememberMe")?.checked;

    if (!name || !pass) {
        alert("Vul naam en pincode in");
        return;
    }

    if (name !== "jonas" && name !== "liese" && name !== "loreana") {
        alert("Onbekende gebruiker");
        return;
    }

    if (remember) {
        localStorage.setItem("homecrew_loggedin", "true");
        localStorage.setItem("homecrewUser", name);
        localStorage.setItem("homecrewPass", pass);
    } else {
        sessionStorage.setItem("homecrew_loggedin", "true");
        sessionStorage.setItem("homecrewUser", name);
        sessionStorage.setItem("homecrewPass", pass);
    }

    window.location.href = "dashboard.html";
}


// =====================
// LOGOUT
// =====================
function logout() {
    localStorage.clear();
    sessionStorage.clear();
    window.location.href = "index.html";
}


// =====================
// TAKEN (GEMEENSCHAPPELIJK)
// =====================

// Taken laden
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
            <strong style="color:${color};font-size:1.1em">${task.title}</strong>
            ${task.desc ? `<p>${task.desc}</p>` : ""}
            <small style="color:${color}">👤 ${task.user}</small>
            <button
              class="delete-btn"
              style="float:right;border:none;background:none;font-size:18px;cursor:pointer;">
              ❌
            </button>
        `;

        li.querySelector(".delete-btn").onclick = () => {
            removeTask(index);
        };

        ul.appendChild(li);
    });
}


// Taak toevoegen
function addTask() {

    const title = document.getElementById("taskTitle").value.trim();
    const desc  = document.getElementById("taskDesc").value.trim();

    const activeUser =
        localStorage.getItem("homecrewUser") ||
        sessionStorage.getItem("homecrewUser");

    if (!title) {
        alert("Vul een titel in");
        return;
    }

    if (!activeUser) {
        alert("Geen gebruiker gevonden, log opnieuw in");
        return;
    }

    const tasks = JSON.parse(localStorage.getItem("homecrew_tasks")) || [];

    tasks.push({
        title,
        desc,
        user: activeUser,
        created: Date.now()
    });

    localStorage.setItem("homecrew_tasks", JSON.stringify(tasks));

    document.getElementById("taskTitle").value = "";
    document.getElementById("taskDesc").value = "";

    loadTasks();
}


// Taak verwijderen
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
