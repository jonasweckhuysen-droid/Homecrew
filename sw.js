const CACHE_NAME = "homecrew-v3";  // <-- elke update dit nummer verhogen

const URLS_TO_CACHE = [
  "/Homecrew/",
  "/Homecrew/index.html",
  "/Homecrew/dashboard.html",
  "/Homecrew/taken.html",
  "/Homecrew/agenda.html",
  "/Homecrew/style.css",
  "/Homecrew/script.js",
  "/Homecrew/manifest.json",
  "/Homecrew/icons/icon-192.png",
  "/Homecrew/icons/icon-512.png"
];

// Install
self.addEventListener("install", event => {
  self.skipWaiting();
  event.waitUntil(
    caches.open(CACHE_NAME).then(cache => {
      return cache.addAll(URLS_TO_CACHE);
    })
  );
});

// Activate (verwijder oude caches)
self.addEventListener("activate", event => {
  event.waitUntil(
    caches.keys().then(keyList => {
      return Promise.all(
        keyList.map(key => {
          if (key !== CACHE_NAME) {
            return caches.delete(key);
          }
        })
      );
    })
  );
});

// Fetch met update-strategie
self.addEventListener("fetch", event => {
  event.respondWith(
    fetch(event.request)
      .then(networkResponse => {
        return caches.open(CACHE_NAME).then(cache => {
          cache.put(event.request, networkResponse.clone());
          return networkResponse;
        });
      })
      .catch(() => caches.match(event.request))
  );
});
