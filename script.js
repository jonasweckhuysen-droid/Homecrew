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
// PAGINA CONTROLE
// =====================
document.addEventListener("DOMContentLoaded", () => {

    const page = window.location.pathname.split("/").pop() || "index.html";
    const logged = localStorage.getItem("homecrew_loggedin") === "true";

    // Alleen redirecten wanneer echt nodig
    if (page === "index.html" && logged) return;

    if (page !== "index.html" && !logged) {
        window.location.href = "index.html";
        return;
    }

    // Taken laden indien nodig
    if (document.getElementById("tasks")) {
        loadTasks();
    }

    // Gebruikerskleur toepassen
    applyUserColor();
});



// =====================
// GEBRUIKER KLEUR TONEN
// =====================
function applyUserColor() {

    const user = localStorage.getItem("homecrewUser");
    const color = getUserColor(user);

    document.querySelectorAll(".user-color").forEach(el => {
        el.style.color = color;
    });

    const header = document.querySelector(".header");
    if (header) header.style.borderBottom = `5px solid ${color}`;
}



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

    if (!["jonas", "liese", "loreana"].includes(name)) {
        alert("Onbekende gebruiker");
        return;
    }

    localStorage.setItem("homecrewUser", name);
    localStorage.setItem("homecrewPass", pass);

    if (remember) {
        localStorage.setItem("homecrew_loggedin", "true");
    } else {
        sessionStorage.setItem("homecrew_loggedin", "true");
    }

    window.location.href = "dashboard.html";
}



// =====================
// LOGOUT
// =====================
function logout() {

    localStorage.removeItem("homecrew_loggedin");
    sessionStorage.removeItem("homecrew_loggedin");
    localStorage.removeItem("homecrewUser");
    localStorage.removeItem("homecrewPass");

    window.location.href = "index.html";
}



// =====================
// NAVIGATIE
// =====================
function goToDashboard() {
    window.location.href = "dashboard.html";
}
function goToTaken() {
    window.location.href = "taken.html";
}
function goToAgenda() {
    window.location.href = "agenda.html";
}
function goToLocatie() {
    window.location.href = "locatie.html";
}
function goToInstellingen() {
    window.location.href = "instellingen.html";
}



// =====================
// TAKEN
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
        li.style.padding = "12px";
        li.style.marginBottom = "12px";
        li.style.borderRadius = "14px";
        li.style.background = "#f9f9f9";
        li.style.listStyle = "none";
        li.style.position = "relative";

        li.innerHTML = `
            <strong style="color:${color};font-size:1.1em">${task.title}</strong>
            ${task.desc ? `<p>${task.desc}</p>` : ""}
            <small style="color:${color}">👤 ${task.user}</small>
            <button   
              class="delete-btn"
              style="
                position:absolute;
                top:10px;
                right:10px;
                border:none;
                background:none;
                font-size:18px;
                cursor:pointer;">
              ❌
            </button>
        `;

        li.querySelector(".delete-btn").addEventListener("click", () => {
            removeTask(index);
        });

        ul.appendChild(li);
    });
}



// Taak toevoegen
function addTask() {

    const title = document.getElementById("taskTitle")?.value.trim();
    const desc  = document.getElementById("taskDesc")?.value.trim();
    const activeUser = localStorage.getItem("homecrewUser");

    if (!title) {
        alert("Vul een titel in");
        return;
    }

    if (!activeUser) {
        alert("Geen gebruiker gevonden. Log opnieuw in.");
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

    if (document.getElementById("taskTitle")) {
        document.getElementById("taskTitle").value = "";
    }

    if (document.getElementById("taskDesc")) {
        document.getElementById("taskDesc").value = "";
    }

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
// GLOBAL EXPORTS
// =====================
window.login = login;
window.logout = logout;
window.addTask = addTask;
window.loadTasks = loadTasks;

window.goToDashboard = goToDashboard;
window.goToTaken = goToTaken;
window.goToAgenda = goToAgenda;
window.goToLocatie = goToLocatie;
window.goToInstellingen = goToInstellingen;
