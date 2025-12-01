// ================= FIREBASE IMPORT =================
import { initializeApp } from "https://www.gstatic.com/firebasejs/10.8.1/firebase-app.js";
import { 
    getFirestore, collection, addDoc, onSnapshot, deleteDoc, doc, updateDoc, query, orderBy 
} from "https://www.gstatic.com/firebasejs/10.8.1/firebase-firestore.js";
import { 
    getAuth, setPersistence, browserLocalPersistence, 
    signInWithEmailAndPassword, createUserWithEmailAndPassword, updatePassword, onAuthStateChanged 
} from "https://www.gstatic.com/firebasejs/10.12.2/firebase-auth.js";

// ================= FIREBASE CONFIG =================
const firebaseConfig = {
    apiKey: "AIzaSyCwlr069pxNXEEhIEGNug5Ly_kKSuvxSzs",
    authDomain: "homecrew-d8e21.firebaseapp.com",
    projectId: "homecrew-d8e21",
    storageBucket: "homecrew-d8e21.firebasestorage.app",
    messagingSenderId: "47664248188",
    appId: "1:47664248188:web:0994c6b2ba07f6fee060f9",
    measurementId: "G-5R58JL5D5K"
};

// ================= INIT =================
const app = initializeApp(firebaseConfig);
const db = getFirestore(app);
const auth = getAuth();

// ================= PERSISTENTIE =================
setPersistence(auth, browserLocalPersistence)
    .then(() => console.log("Gebruiker wordt onthouden op dit toestel"))
    .catch(err => console.error("Fout persistentie:", err));

// ================= CHECK LOCALSTORAGE =================
document.addEventListener("DOMContentLoaded", () => {
    const storedUser = localStorage.getItem("homecrewUser");
    const registerSection = document.getElementById("register-section");
    const loginSection = document.getElementById("login-section");

    if (!storedUser) {
        // Geen gebruiker bekend => eerste keer registratie tonen
        registerSection.style.display = "block";
        loginSection.style.display = "none";
    } else {
        // Gebruiker bekend => automatisch inloggen
        loginSection.style.display = "block";
        registerSection.style.display = "none";
        autoLogin(storedUser);
    }
});

// ================= AUTOMATISCH INLOGGEN =================
async function autoLogin(user) {
    try {
        const pass = localStorage.getItem("homecrewPass");
        if (!pass) return console.log("Geen opgeslagen wachtwoord, login handmatig");

        const cred = await signInWithEmailAndPassword(auth, user + "@homecrew.local", pass);
        console.log("Automatisch ingelogd:", cred.user.email);
        localStorage.setItem("homecrewUser", user);
        window.location.href = "dashboard.html";
    } catch (err) {
        console.error("Automatisch inloggen mislukt:", err);
    }
}

// ================= LOGIN =================
window.login = async function() {
    const user = document.getElementById("loginUsername").value.trim().toLowerCase();
    const pass = document.getElementById("loginPassword").value.trim();
    const errorEl = document.getElementById("error");

    if (!user || !pass) {
        if (errorEl) errorEl.textContent = "Vul gebruikersnaam en wachtwoord in";
        return;
    }

    try {
        const cred = await signInWithEmailAndPassword(auth, user + "@homecrew.local", pass);
        console.log("Ingelogd als:", cred.user.email);
        localStorage.setItem("homecrewUser", user);
        localStorage.setItem("homecrewPass", pass); // Voor automatisch inloggen
        window.location.href = "dashboard.html";
    } catch (err) {
        console.error(err);
        if (errorEl) errorEl.textContent = "Ongeldige login of gebruiker bestaat niet";
    }
};

// ================= REGISTREREN =================
window.registerUser = async function() {
    const user = document.getElementById("username").value.trim().toLowerCase();
    const pass = document.getElementById("password").value.trim();

    if (!user || !pass) return alert("Vul gebruikersnaam en wachtwoord in");

    try {
        const cred = await createUserWithEmailAndPassword(auth, user + "@homecrew.local", pass);
        console.log("Account aangemaakt:", cred.user.email);
        localStorage.setItem("homecrewUser", user);
        localStorage.setItem("homecrewPass", pass); // Opslaan voor automatisch inloggen
        window.location.href = "dashboard.html";
    } catch (err) {
        console.error(err);
        alert("Account kon niet aangemaakt worden: " + err.message);
    }
};

// ================= WACHTWOORD WIJZIGEN =================
window.changePassword = async function(newPass) {
    if (!newPass || newPass.length < 4) return alert("Voer een geldig wachtwoord in");
    const user = auth.currentUser;
    if (!user) return alert("Je moet ingelogd zijn om het wachtwoord te wijzigen");

    try {
        await updatePassword(user, newPass);
        localStorage.setItem("homecrewPass", newPass); // Update lokaal opgeslagen wachtwoord
        alert("Wachtwoord succesvol gewijzigd!");
    } catch (err) {
        console.error(err);
        alert("Wachtwoord kon niet gewijzigd worden: " + err.message);
    }
};

// ================= LOGOUT =================
window.logout = async function() {
    try {
        await auth.signOut();
        localStorage.removeItem("homecrewUser");
        localStorage.removeItem("homecrewPass");
        window.location.href = "index.html";
    } catch (err) {
        console.error("Logout mislukt:", err);
    }
};
export { auth, logout };

// ===================== TAKEN FUNCTIES =====================

// Taken laden
export function loadTasks() {
    const activeUser = localStorage.getItem("homecrewUser");
    const tasks = JSON.parse(localStorage.getItem(`tasks_${activeUser}`)) || [];
    const ul = document.getElementById("tasks");
    if (!ul) return; // check of ul bestaat
    ul.innerHTML = "";

    tasks.forEach((task, index) => {
        const li = document.createElement("li");
        li.innerHTML = `
            <strong>${task.title}</strong>
            <p>${task.desc}</p>
            <button class="delete-btn">❌</button>
        `;
        li.querySelector(".delete-btn").addEventListener("click", () => {
            removeTask(index);
        });
        ul.appendChild(li);
    });
}

// Taak toevoegen
export function addTask() {
    const title = document.getElementById("taskTitle").value.trim();
    const desc = document.getElementById("taskDesc").value.trim();

    if (!title) {
        alert("Vul een titel in voor de taak");
        return;
    }

    const activeUser = localStorage.getItem("homecrewUser");
    const tasks = JSON.parse(localStorage.getItem(`tasks_${activeUser}`)) || [];

    tasks.push({ title, desc });
    localStorage.setItem(`tasks_${activeUser}`, JSON.stringify(tasks));

    document.getElementById("taskTitle").value = "";
    document.getElementById("taskDesc").value = "";

    loadTasks();
}

// Taak verwijderen
export function removeTask(index) {
    const activeUser = localStorage.getItem("homecrewUser");
    const tasks = JSON.parse(localStorage.getItem(`tasks_${activeUser}`)) || [];
    tasks.splice(index, 1);
    localStorage.setItem(`tasks_${activeUser}`, JSON.stringify(tasks));
    loadTasks();
}
