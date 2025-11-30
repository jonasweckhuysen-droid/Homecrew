const users = {
    "jonas": "1234",
    "liese": "1234",
    "loreana": "1234"
};

// ================= LOGIN =================
function login() {
    const user = document.getElementById("username").value;
    const pass = document.getElementById("password").value;

    if (users[user] && users[user] === pass) {
        localStorage.setItem("homecrewUser", user);
        window.location.href = "agenda.html";
    } else {
        document.getElementById("error").textContent = "Ongeldige login";
    }
}

// ================= LOGOUT =================
function logout() {
    localStorage.removeItem("homecrewUser");
    window.location.href = "index.html";
}

// ================= LOAD USER =================
if (window.location.pathname.includes("agenda.html")) {
    const activeUser = localStorage.getItem("homecrewUser");

    if (!activeUser) {
        window.location.href = "index.html";
    }

    document.getElementById("welcome").innerText =
        "Welkom, " + activeUser + " 👋";

    loadEvents();
}

// ================= AGENDA =================
function addEvent() {
    const date = document.getElementById("date").value;
    const title = document.getElementById("title").value;
    const description = document.getElementById("description").value;
    const user = localStorage.getItem("homecrewUser");

    if (!date || !title) {
        alert("Datum en titel zijn verplicht");
        return;
    }

    const event = {
        date,
        title,
        description,
        user
    };

    let events = JSON.parse(localStorage.getItem("homecrewEvents")) || [];
    events.push(event);

    localStorage.setItem("homecrewEvents", JSON.stringify(events));

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
