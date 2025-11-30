const CACHE_NAME = "homecrew-cache-v1";
const urlsToCache = [
    "/index.html",
    "/dashboard.html",
    "/agenda.html",
    "/taken.html",
    "/style.css",
    "/script.js",
    "/manifest.json"
];

// Installeren
self.addEventListener("install", event => {
    event.waitUntil(
        caches.open(CACHE_NAME)
            .then(cache => cache.addAll(urlsToCache))
    );
});

// Cache bijwerken
self.addEventListener("activate", event => {
    event.waitUntil(
        caches.keys().then(keys =>
            Promise.all(keys.filter(key => key !== CACHE_NAME)
                .map(key => caches.delete(key))
            )
        )
    );
});

// Requests afhandelen
self.addEventListener("fetch", event => {
    event.respondWith(
        caches.match(event.request)
            .then(response => response || fetch(event.request))
    );
});
