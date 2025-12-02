import fs from "fs";
import fetch from "node-fetch";
import ical from "ical";
import admin from "firebase-admin";
import cron from "node-cron";

// === FIREBASE ADMIN INITIALISEREN ===
const serviceAccount = JSON.parse(fs.readFileSync("./serviceAccountKey.json", "utf8"));

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();
const eventsCol = db.collection("homecrewEvents");

// === GOOGLE CALENDAR ICS URL ===
const icsUrl = "https://calendar.google.com/calendar/ical/b3756ae16d0f11ea12a33407c6077e5925740ab0ce0d19514aa77a7fc6a6e0c0%40group.calendar.google.com/public/basic.ics";

// === FUNCTIE: IMPORTEREN ===
async function importCalendar() {
  try {
    console.log("Start import Google Calendar...");

    const res = await fetch(icsUrl);
    const icsData = await res.text();

    const parsed = ical.parseICS(icsData);
    let addedCount = 0;

    for (const k in parsed) {
      const ev = parsed[k];
      if (ev.type === "VEVENT") {
        const title = ev.summary;
        const date = ev.start.toISOString();
        const description = ev.description || "";

        // Controleer of event al bestaat
        const snapshot = await eventsCol
          .where("title", "==", title)
          .where("date", "==", date)
          .get();

        if (snapshot.empty) {
          await eventsCol.add({
            user: "Google Agenda",
            title,
            date,
            description,
            timestamp: admin.firestore.FieldValue.serverTimestamp()
          });
          addedCount++;
          console.log(`Toegevoegd: ${title} (${date})`);
        }
      }
    }

    console.log(`Import klaar! Nieuwe events toegevoegd: ${addedCount}`);
  } catch (err) {
    console.error("Fout bij importeren:", err);
  }
}

// === CRON JOB: AUTOMATISCH ELKE 5 MINUTEN ===
cron.schedule("*/5 * * * *", () => {
  importCalendar();
});

// Eerste keer meteen draaien
importCalendar();
