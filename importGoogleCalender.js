import fs from "fs";
import fetch from "node-fetch";
import ical from "ical";
import admin from "firebase-admin";

// === FIREBASE ADMIN INITIALISEREN ===
const serviceAccount = JSON.parse(fs.readFileSync("./serviceAccountKey.json", "utf8"));

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();
const eventsCol = db.collection("homecrewEvents");

// === GOOGLE CALENDAR ICS URL ===
const icsUrl = "https://calendar.google.com/calendar/ical/YOUR_CALENDAR_ID/public/basic.ics";

// === IMPORTEREN ===
async function importCalendar() {
  try {
    const res = await fetch(icsUrl);
    const icsData = await res.text();

    const parsed = ical.parseICS(icsData);

    for (const k in parsed) {
      const ev = parsed[k];
      if (ev.type === "VEVENT") {
        const title = ev.summary;
        const date = ev.start.toISOString();
        const description = ev.description || "";

        // Check of event al bestaat in Firestore
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
          console.log(`Toegevoegd: ${title} (${date})`);
        } else {
          console.log(`Bestaat al: ${title} (${date})`);
        }
      }
    }

    console.log("Import klaar!");
  } catch (err) {
    console.error("Fout bij importeren:", err);
  }
}

// === SCRIPT RUNNEN ===
importCalendar();
