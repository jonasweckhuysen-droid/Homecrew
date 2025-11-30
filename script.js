const users = {
    "Jonas": "1234",
    "Liese": "1234",
    "Loreana": "1234"
};

function login() {
    const user = document.getElementById("username").value.toLowerCase();
    const pass = document.getElementById("password").value;

    if (users[user] && users[user] === pass) {
        localStorage.setItem("homecrewUser", user);
        window.location.href = "agenda.html";
    } else {
        document.getElementById("error").textContent = "Ongeldige login";
    }
}
