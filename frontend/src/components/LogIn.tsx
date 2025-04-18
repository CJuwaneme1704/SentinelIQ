'use client';

import Link from "next/link";

export default function LogIn() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="bg-white shadow-md rounded-lg p-8 max-w-md w-full">
        <h1 className="text-2xl font-bold text-purple-600 text-center mb-6">
          Welcome Back to SentinelIQ
        </h1>
        <form className="space-y-4">
          {/* Email Input */}
          <div>
            <label htmlFor="email" className="block text-sm font-medium text-gray-700">
              Email Address
            </label>
            <input
              type="email"
              id="email"
              name="email"
              className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-purple-500 focus:border-purple-500"
              placeholder="you@example.com"
              required
            />
          </div>

          {/* Password Input */}
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
              required
            />
          </div>

          {/* Submit Button */}
          <button
            type="submit"
            className="w-full bg-purple-600 text-white py-2 px-4 rounded-md font-medium hover:bg-purple-800 transition duration-300"
          >
            Log In
          </button>
        </form>

        {/* Additional Links */}
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