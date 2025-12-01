/*************************
  HOME CREW - SCRIPT.JS
**************************/

// =====================
// HELPER FUNCTIES
// =====================

// Kleur per gebruiker
function getUserColor(user) {
    if (user === "jonas") return "#3498db";   // blauw
    if (user === "liese") return "#e74c3c";   // rood
    if (user === "loreana") return "#ff69b4"; // roze
    return "#555"; // standaard
}

// Check of gebruiker ingelogd is
function isLoggedIn() {
    return (
        localStorage.getItem("homecrew_loggedin") === "true" ||
        sessionStorage.getItem("homecrew_loggedin") === "true"
    );
}

// Huidige actieve gebruiker ophalen
function getActiveUser() {
    return localStorage.getItem("homecrewUser") || sessionStorage.getItem("homecrewUser");
}

// =====================
// LOGIN
// =====================
function login(username, password, stayLoggedIn) {
    if (!username || !password) {
        alert("Vul naam en pincode in");
        return false;
    }

    const name = username.trim().toLowerCase();

    // Alleen bekende gebruikers
    if (!["jonas", "liese", "loreana"].includes(name)) {
        alert("Onbekende gebruiker");
        return false;
    }

    if (stayLoggedIn) {
        localStorage.setItem("homecrew_loggedin", "true");
        localStorage.setItem("homecrewUser", name);
        localStorage.setItem("homecrewPass", password);
    } else {
        sessionStorage.setItem("homecrew_loggedin", "true");
        sessionStorage.setItem("homecrewUser", name);
        sessionStorage.setItem("homecrewPass", password);
    }

    window.location.href = "dashboard.html";
    return true;
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
        li.style.padding = "10px";
        li.style.marginBottom = "12px";
        li.style.borderRadius = "12px";
        li.style.background = "#f9f9f9";
        li.style.listStyle = "none";

        li.innerHTML = `
            <strong style="color:${color};font-size:1.1em">${task.title}</strong>
            ${task.desc ? `<p>${task.desc}</p>` : ""}
            <small style="color:${color}">👤 ${task.user}</small>
            <button class="delete-btn" style="float:right;border:none;background:none;font-size:18px;cursor:pointer;">❌</button>
        `;

        li.querySelector(".delete-btn").onclick = () => removeTask(index);

        ul.appendChild(li);
    });
}

// Taak toevoegen
function addTask(title, desc) {
    const activeUser = getActiveUser();
    if (!title || !activeUser) return;

    const tasks = JSON.parse(localStorage.getItem("homecrew_tasks")) || [];
    tasks.push({ title, desc, user: activeUser, created: Date.now() });
    localStorage.setItem("homecrew_tasks", JSON.stringify(tasks));
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
// EXPORT
// =====================
window.login = login;
window.logout = logout;
window.addTask = addTask;
window.loadTasks = loadTasks;
window.isLoggedIn = isLoggedIn;
window.getActiveUser = getActiveUser;
