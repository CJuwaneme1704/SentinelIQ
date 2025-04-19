'use client';

import Link from "next/link";


export default function Navbar() {


  
  return (
    <header className="bg-white shadow-md py-4 px-6">
      <nav className="max-w-7xl mx-auto flex justify-between items-center">
        {/* Brand */}
        <Link href="/" className="text-xl font-bold text-purple-600 hover:text-purple-800 transition duration-300">
          SentinelIQ
        </Link>

        {/* Nav Links */}
        <div className="flex space-x-6 text-sm md:text-base items-center">
          <Link href="/docs" className="text-purple-600 font-medium hover:text-purple-800 hover:underline transition duration-300">
            Docs
          </Link>
          <Link
            href="/login"
            className="text-purple-600 font-medium hover:text-purple-800 hover:underline transition duration-300"
          >
            Login
          </Link>
          {/* Get Started Free Button */}
          <Link
            href="/signup"
            className="bg-purple-600 text-white px-4 py-2 rounded-md text-sm md:text-base font-medium hover:bg-purple-800 transition duration-300"
          >
            Get Started Free
          </Link>
        </div>
      </nav>
    </header>
  );
}
