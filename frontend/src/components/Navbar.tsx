'use client';

import Link from "next/link";
import { useAuth } from "@/context/AuthContext"; // 🔑 Auth context

export default function Navbar() {
  const { isAuthenticated, setIsAuthenticated, authChecked } = useAuth(); // ✅ include authChecked

  // 🛑 Wait until auth status is checked (prevents flashing wrong links)
  if (!authChecked) return null;

  const handleLogout = async () => {
    try {
      await fetch("http://localhost:8080/api/auth/logout", {
        method: "POST",
        credentials: "include",
      });
      setIsAuthenticated(false);
      window.location.href = "/";
    } catch (err) {
      console.error("Logout failed:", err);
    }
  };

  return (
    <header className="bg-white shadow-md py-4 px-6">
      <nav className="max-w-7xl mx-auto flex justify-between items-center">
        <Link
          href="/"
          className="text-xl font-bold text-purple-600 hover:text-purple-800 transition duration-300"
        >
          SentinelIQ
        </Link>

        <div className="flex space-x-6 text-sm md:text-base items-center">
          <Link
            href="/docs"
            className="text-purple-600 font-medium hover:text-purple-800 hover:underline transition duration-300"
          >
            Docs
          </Link>

          {isAuthenticated ? (
            <>
              {/* Optional: add dashboard link */}
              {/* <Link
                href="/user_pages/protected/dashboard"
                className="text-purple-600 font-medium hover:text-purple-800 hover:underline transition duration-300"
              >
                Dashboard
              </Link> */}
              <button
                onClick={handleLogout}
                className="text-red-600 font-medium hover:text-red-800 hover:underline transition duration-300"
              >
                Logout
              </button>
            </>
          ) : (
            <>
              <Link
                href="/login"
                className="text-purple-600 font-medium hover:text-purple-800 hover:underline transition duration-300"
              >
                Login
              </Link>
              <Link
                href="/signup"
                className="bg-purple-600 text-white px-4 py-2 rounded-md text-sm md:text-base font-medium hover:bg-purple-800 transition duration-300"
              >
                Get Started For Free
              </Link>
            </>
          )}
        </div>
      </nav>
    </header>
  );
}
