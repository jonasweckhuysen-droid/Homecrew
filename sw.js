const CACHE_NAME = "homecrew-cache-v1";
const urlsToCache = [
    "/Homecrew/index.html",
    "/Homecrew/dashboard.html",
    "/Homecrew/agenda.html",
    "/Homecrew/taken.html",
    "/Homecrew/style.css",
    "/Homecrew/script.js",
    "/Homecrew/manifest.json"
];

self.addEventListener("install", event => {
    event.waitUntil(
        caches.open(CACHE_NAME).then(cache => cache.addAll(urlsToCache))
    );
});

self.addEventListener("activate", event => {
    event.waitUntil(
        caches.keys().then(keys =>
            Promise.all(
                keys.filter(key => key !== CACHE_NAME)
                    .map(key => caches.delete(key))
            )
        )
    );
});

self.addEventListener("fetch", event => {
    event.respondWith(
        caches.match(event.request).then(response => response || fetch(event.request))
    );
});
