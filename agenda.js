// =====================================================
// agenda.js — Homecrew Agenda: lokaal + Google ICS
// =====================================================

const CACHE_KEY = "homecrewLocalEvents"; // lokaal opgeslagen events
const ICS_URL = "https://calendar.google.com/calendar/ical/b3756ae16d0f11ea12a33407c6077e5925740ab0ce0c19514aa77a7fc6a6e0c0%40group.calendar.google.com/public/basic.ics";
const PROXY_URL = "https://api.allorigins.win/raw?url=" + encodeURIComponent(ICS_URL);
const REFRESH_INTERVAL = 5 * 60 * 1000; // 5 minuten

// =====================
// Get user kleur
// =====================
function getUserColor(u){
    if (!u) return "#999";
    const s = u.toLowerCase();
    if (s === "jonas") return "#3498db";
    if (s === "liese") return "#e74c3c";
    if (s === "loreana") return "#ff69b4";
    return "#777";
}

// =====================
// Escape HTML
// =====================
function escapeHtml(str){
    if (!str) return "";
    return String(str)
        .replace(/&/g,"&amp;")
        .replace(/</g,"&lt;")
        .replace(/>/g,"&gt;")
        .replace(/"/g,"&quot;")
        .replace(/'/g,"&#39;");
}

// =====================
// Laad events: lokaal + ICS
// =====================
async function loadEvents(){
    const list = document.getElementById("eventList");
    if (!list) return;
    list.innerHTML = "";

    // --- 1) lokale events ---
    const localEvents = JSON.parse(localStorage.getItem(CACHE_KEY) || "[]");
    localEvents.sort((a,b)=> new Date(a.date) - new Date(b.date));
    for (const ev of localEvents){
        const li = document.createElement("li");
        li.className = "event-item";
        li.style.borderLeft = "6px solid " + (ev.user ? getUserColor(ev.user) : "#3498db");
        li.innerHTML = `
            <strong>${escapeHtml(ev.title)}</strong>
            <div class="event-meta">${new Date(ev.date).toLocaleString("nl-BE")}</div>
            <div class="event-user" style="color:${getUserColor(ev.user)}">👤 ${escapeHtml(ev.user)}</div>
            <div>${escapeHtml(ev.description || "")}</div>
        `;
        list.appendChild(li);
    }

    // --- 2) ICS via proxy ---
    try {
        const res = await fetch(PROXY_URL);
        if (!res.ok) throw new Error("ICS kon niet geladen worden");

        const icsText = await res.text();
        const jcalData = ICAL.parse(icsText);
        const comp = new ICAL.Component(jcalData);
        const vevents = comp.getAllSubcomponents("vevent");

        vevents.forEach(v => {
            const ev = new ICAL.Event(v);
            const start = ev.startDate.toJSDate();
            if (start < new Date()) return; // skip oude events

            // fallback: title uit summary of eerste regel description
            let title = ev.summary || "";
            if (!title && ev.description) title = ev.description.split("\n")[0];
            if (!title) title = "Geen titel";

            const li = document.createElement("li");
            li.className = "event-item google-event";
            li.innerHTML = `
                <strong>${escapeHtml(title)}</strong>
                <div class="event-meta">${start.toLocaleString("nl-BE")}</div>
                <div style="color:black;">📅 Google Agenda</div>
                ${ev.location ? `<div>📍 ${escapeHtml(ev.location)}</div>` : ""}
            `;
            list.appendChild(li);
        });

    } catch(err){
        console.error("Fout bij ICS laden:", err);
        const li = document.createElement("li");
        li.className = "event-item google-event";
        li.innerText = "Kon Google Agenda niet laden (check console)";
        list.appendChild(li);
    }
}

// =====================
// Auto-refresh
// =====================
setInterval(loadEvents, REFRESH_INTERVAL);
document.addEventListener("DOMContentLoaded", loadEvents);
