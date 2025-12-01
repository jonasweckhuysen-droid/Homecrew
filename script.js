/*************************
  HOME CREW - SCRIPT.JS
**************************/

// =====================
// GEBRUIKER KLEUR
// =====================
function getUserColor(user) {
    if (user.toLowerCase() === "jonas") return "#3498db";   // blauw
    if (user.toLowerCase() === "liese") return "#e74c3c";   // rood
    if (user.toLowerCase() === "loreana") return "#ff69b4"; // roze
    return "#555"; // standaard
}

// =====================
// LOGIN CHECK
// =====================
function isLoggedIn() {
    return localStorage.getItem("homecrew_loggedin") === "true" ||
           sessionStorage.getItem("homecrew_loggedin") === "true";
}

// =====================
// LOGIN FUNCTIE
// =====================
function login(username, password, stayLoggedIn) {
    username = username.trim().toLowerCase();
    password = password.trim();

    const errorEl = document.getElementById("error");

    if (!username || !password) {
        if (errorEl) errorEl.textContent = "Vul naam en pincode in";
        return;
    }

    // Toegestane gebruikers
    const allowedUsers = ["jonas", "liese", "loreana"];
    if (!allowedUsers.includes(username)) {
        if (errorEl) errorEl.textContent = "Onbekende gebruiker";
        return;
    }

    if (stayLoggedIn) {
        localStorage.setItem("homecrew_loggedin", "true");
        localStorage.setItem("homecrewUser", username);
        localStorage.setItem("homecrewPass", password);
    } else {
        sessionStorage.setItem("homecrew_loggedin", "true");
        sessionStorage.setItem("homecrewUser", username);
        sessionStorage.setItem("homecrewPass", password);
    }

    // Ga naar dashboard
    window.location.href = "dashboard.html";
}

// =====================
// LOGOUT FUNCTIE
// =====================
function logout() {
    localStorage.removeItem("homecrew_loggedin");
    localStorage.removeItem("homecrewUser");
    localStorage.removeItem("homecrewPass");

    sessionStorage.removeItem("homecrew_loggedin");
    sessionStorage.removeItem("homecrewUser");
    sessionStorage.removeItem("homecrewPass");

    window.location.href = "index.html";
}

// =====================
// TAKEN FUNCTIES
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
            <strong style="color:${color}; font-size:1.1em">${task.title}</strong>
            ${task.desc ? `<p>${task.desc}</p>` : ""}
            <small style="color:${color}">👤 ${task.user}</small>
            <button class="delete-btn" style="float:right;border:none;background:none;font-size:18px;cursor:pointer;">❌</button>
        `;

        li.querySelector(".delete-btn").onclick = () => removeTask(index);
        ul.appendChild(li);
    });
}

function addTask() {
    const title = document.getElementById("taskTitle").value.trim();
    const desc = document.getElementById("taskDesc").value.trim();
    const user = localStorage.getItem("homecrewUser") || sessionStorage.getItem("homecrewUser");

    if (!title) {
        alert("Vul een titel in");
        return;
    }
    if (!user) {
        alert("Geen gebruiker gevonden, log opnieuw in");
        return;
    }

    const tasks = JSON.parse(localStorage.getItem("homecrew_tasks")) || [];
    tasks.push({ title, desc, user, created: Date.now() });
    localStorage.setItem("homecrew_tasks", JSON.stringify(tasks));

    document.getElementById("taskTitle").value = "";
    document.getElementById("taskDesc").value = "";
    loadTasks();
}

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
window.isLoggedIn = isLoggedIn;

// =====================
// AUTOMATISCH TAKEN LADEN
// =====================
document.addEventListener("DOMContentLoaded", () => {
    if (document.getElementById("tasks")) loadTasks();
});
