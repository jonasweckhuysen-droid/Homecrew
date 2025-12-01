/*************************
  HOME CREW - SCRIPT.JS (simpel)
**************************/

// ✅ Check of gebruiker ingelogd is bij pagina load
document.addEventListener("DOMContentLoaded", () => {
    const page = window.location.pathname.split("/").pop();
    const loggedUser = localStorage.getItem("homecrewUser");

    // Als ingelogd en op index → naar dashboard
    if ((page === "" || page === "index.html") && loggedUser) {
        window.location.href = "dashboard.html";
    }

    // Als niet ingelogd en niet op index → terug naar index
    if (page !== "index.html" && !loggedUser) {
        window.location.href = "index.html";
    }

    // Taken automatisch laden als lijst aanwezig
    if (document.getElementById("tasks")) {
        loadTasks();
    }

    // Welkom op dashboard
    const welcomeEl = document.getElementById("welcome");
    if (welcomeEl && loggedUser) {
        welcomeEl.innerText = `Welkom, ${loggedUser} 👋`;
    }
});

// =====================
// LOGIN
// =====================
function login() {
    const name = document.getElementById("username").value.trim();
    const pass = document.getElementById("password").value.trim();

    if (!name || !pass) {
        alert("Vul naam en wachtwoord in");
        return;
    }

    // Eenvoudige gebruikerslijst (hardcoded)
    const users = {
        jonas: "1234",
        liese: "1234",
        loreana: "1234"
    };

    if (!users[name.toLowerCase()] || users[name.toLowerCase()] !== pass) {
        alert("Onbekende gebruiker of verkeerd wachtwoord");
        return;
    }

    // Opslaan ingelogde gebruiker
    localStorage.setItem("homecrewUser", name.toLowerCase());

    // Redirect naar dashboard
    window.location.href = "dashboard.html";
}

// =====================
// LOGOUT
// =====================
function logout() {
    localStorage.removeItem("homecrewUser");
    window.location.href = "index.html";
}

// =====================
// TAKEN
// =====================
function getUserColor(user) {
    const colors = { jonas: "#3498db", liese: "#e74c3c", loreana: "#ff69b4" };
    return colors[user.toLowerCase()] || "#555";
}

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
    const activeUser = localStorage.getItem("homecrewUser");

    if (!title) return alert("Vul een titel in");

    const tasks = JSON.parse(localStorage.getItem("homecrew_tasks")) || [];
    tasks.push({ title, desc, user: activeUser });
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

// Exporteer functies
window.login = login;
window.logout = logout;
window.addTask = addTask;
window.loadTasks = loadTasks;
