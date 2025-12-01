// ================= FIREBASE IMPORT =================
import { initializeApp } from "https://www.gstatic.com/firebasejs/10.8.1/firebase-app.js";
import { getAuth, setPersistence, browserLocalPersistence, updatePassword } 
    from "https://www.gstatic.com/firebasejs/10.12.2/firebase-auth.js";

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
const auth = getAuth();

// ================= PERSISTENTIE =================
setPersistence(auth, browserLocalPersistence)
    .then(() => console.log("Gebruiker wordt onthouden op dit toestel"))
    .catch(err => console.error("Fout persistentie:", err));

// ================= CHECK AUTOMATISCH INLOGGEN =================
const storedUser = localStorage.getItem("homecrewUser");
if (storedUser) {
    showDashboard(storedUser); // automatisch naar dashboard
} else {
    showRegisterForm(); // eerste keer → pincode kiezen
}

// ================= FUNCTIES =================
function showDashboard(user) {
    localStorage.setItem("homecrewUser", user);
    const welcomeEl = document.getElementById("welcome");
    if (welcomeEl) welcomeEl.textContent = "Welkom, " + user + " 👋";
    window.location.href = "dashboard.html";
}

function showRegisterForm() {
    document.getElementById("register-section").style.display = "block";
}

// ================= GEBRUIKER KIEZEN / REGISTREREN =================
window.registerUser = function() {
    const user = document.getElementById("username").value.trim();
    const pass = document.getElementById("password").value.trim();

    if (!user || !pass) return alert("Vul een gebruikersnaam en wachtwoord/pincode in");

    // Firebase account aanmaken voor veilige opslag van wachtwoord
    // Hier gebruiken we een “dummy” email omdat we enkel pincode willen gebruiken
    const email = user.toLowerCase() + "@homecrew.local";

    createUserInFirebase(email, pass)
        .then(() => {
            showDashboard(user);
        })
        .catch(err => {
            console.error(err);
            alert("Kon gebruiker niet registreren: " + err.message);
        });
};

// ================= WACHTWOORD WIJZIGEN =================
window.changePassword = async function(newPass) {
    if (!newPass || newPass.length < 4) return alert("Voer een geldig wachtwoord in");
    const user = auth.currentUser;
    if (!user) return alert("Je moet ingelogd zijn om het wachtwoord te wijzigen");

    try {
        await updatePassword(user, newPass);
        alert("Wachtwoord succesvol gewijzigd!");
    } catch (err) {
        console.error(err);
        alert("Wachtwoord kon niet gewijzigd worden: " + err.message);
    }
};

// ================= LOGOUT =================
window.logout = function() {
    localStorage.removeItem("homecrewUser");
    window.location.href = "index.html";
};

// ================= HELPER: Firebase registratie =================
async function createUserInFirebase(email, password) {
    try {
        // Firebase heeft email + wachtwoord nodig, maar we gebruiken dummy email
        const { user } = await auth.createUserWithEmailAndPassword(auth, email, password);
        return user;
    } catch (err) {
        // Als gebruiker al bestaat → gewoon accepteren
        if (err.code === "auth/email-already-in-use") return auth.signInWithEmailAndPassword(email, password);
        throw err;
    }
}
