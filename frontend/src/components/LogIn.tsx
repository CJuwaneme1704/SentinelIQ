'use client';

import { useState } from "react";
import Link from "next/link";
// import { useRouter } from "next/navigation";

export default function LogIn() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  // const router = useRouter();

  async function handleLogin(e: React.FormEvent) {
    e.preventDefault();

    try {
      const res = await fetch("http://localhost:8080/api/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include", // 🔐 needed for cookie exchange
        body: JSON.stringify({ username, password }),
      });
      
      const data = await res.json();

      if (!res.ok) {
        alert("Login failed: " + (data.message || "Unknown error"));
      } else {
        // localStorage.setItem("token", data.token); // 🔐 Store JWT for future requests
        alert("Login successful!");
        // router.push("/dashboard"); // Optional: redirect after login
      }
    } catch (error) {
      console.error("Something went wrong", error);
      alert("An error occurred. Please try again.");
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="bg-white shadow-md rounded-lg p-8 max-w-md w-full">
        <h1 className="text-2xl font-bold text-purple-600 text-center mb-6">
          Welcome to SentinelIQ
        </h1>
        <form className="space-y-4" onSubmit={handleLogin}>
          <div>
            <label htmlFor="username" className="block text-sm font-medium text-gray-700">
              Username
            </label>
            <input
              type="text"
              id="username"
              name="username"
              className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-purple-500 focus:border-purple-500"
              placeholder="Your username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>

          <div>
            <label htmlFor="password" className="block text-sm font-medium text-gray-700">
              Password
            </label>
            <input
              type="password"
              id="password"
              name="password"
              className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-purple-500 focus:border-purple-500"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <button
            type="submit"
            className="w-full bg-purple-600 text-white py-2 px-4 rounded-md font-medium hover:bg-purple-800 transition duration-300"
          >
            Log In
          </button>
        </form>

        <div className="mt-6 text-center text-sm text-gray-600">
          <p>
            Don&apos;t have an account?{" "}
            <Link href="/signup" className="text-purple-600 hover:underline">
              Sign Up
            </Link>
          </p>
          <p className="mt-2">
            <Link href="/forgot-password" className="text-purple-600 hover:underline">
              Forgot your password?
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
