/*************************
 HOMECREW - SCRIPT.JS
**************************/

// =====================
// LOGIN CHECK
// =====================
function isLoggedIn() {
    return !!(localStorage.getItem("homecrewUser") || sessionStorage.getItem("homecrewUser"));
}

function login(username, password, stayLoggedIn) {
    if (!username || !password) {
        alert("Vul naam en wachtwoord/pincode in");
        return;
    }

    // Bestaande users ophalen of nieuwe maken
    const users = JSON.parse(localStorage.getItem("homecrewUsers") || "{}");

    if (users[username] && users[username] !== password) {
        alert("Onjuist wachtwoord!");
        return;
    }

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
    const user = localStorage.getItem("homecrewUser") || sessionStorage.getItem("homecrewUser");

    tasks.forEach((task, index) => {
        const li = document.createElement("li");
        li.innerHTML = `<strong>${task.title}</strong><p>${task.desc}</p><small>${task.user}</small>
                        <button class="delete-btn">❌</button>`;
        li.querySelector(".delete-btn").onclick = () => removeTask(index);
        ul.appendChild(li);
    });
}

function addTask() {
    const title = document.getElementById("taskTitle")?.value.trim();
    const desc = document.getElementById("taskDesc")?.value.trim();
    if (!title) return alert("Vul een titel in");

    const user = localStorage.getItem("homecrewUser") || sessionStorage.getItem("homecrewUser");
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
// EVENT LISTENERS TOEVOEGEN
// =====================
document.addEventListener("DOMContentLoaded", () => {
    // Dashboard welcome
    const welcomeEl = document.getElementById("welcome");
    const user = localStorage.getItem("homecrewUser") || sessionStorage.getItem("homecrewUser");
    if (welcomeEl && user) welcomeEl.innerText = "Welkom, " + user + " 👋";

    // Knoppen
    const addBtn = document.getElementById("addTaskBtn");
    if (addBtn) addBtn.onclick = addTask;

    const logoutBtn = document.getElementById("logoutBtn");
    if (logoutBtn) logoutBtn.onclick = logout;

    const btnTaken = document.getElementById("btnTaken");
    if (btnTaken) btnTaken.onclick = () => window.location.href = "taken.html";
});
