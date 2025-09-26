// auth.js

// Fetches current user info from session-based API
async function fetchCurrentUser() {
    try {
        const res = await fetch("/api/current-user", { credentials: "include" });
        if (!res.ok) {
            localStorage.removeItem("currentUser");
            return null;
        }
        const user = await res.json();
        user.isAdmin = user.role?.toUpperCase() === "ADMIN";
        localStorage.setItem("currentUser", JSON.stringify(user));
        return user;
    } catch (err) {
        console.error("Failed to fetch current user:", err);
        localStorage.removeItem("currentUser");
        return null;
    }
}

// Gets current user from localStorage
function getCurrentUser() {
    const user = localStorage.getItem("currentUser");
    return user ? JSON.parse(user) : null;
}

// Logs out the current user and redirects to index.html
async function logout() {
    try {
        const res = await fetch("/api/logout", {
            method: "POST",
            credentials: "include"
        });

        // Clear user data regardless of backend result
        localStorage.removeItem("currentUser");

        if (res.ok) {
            // Optional: alert can be skipped if redirecting immediately
            console.log("✅ Logged out successfully.");
        } else {
            console.warn("⚠️ Logout request failed. Proceeding to redirect anyway.");
        }

        // Always redirect to homepage after logout
        window.location.href = "index.html";

    } catch (err) {
        console.error("Logout error:", err);
        alert("An error occurred while logging out. Redirecting to homepage...");
        window.location.href = "index.html";  // Fallback redirect
    }
}
