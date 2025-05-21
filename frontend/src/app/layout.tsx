// app/layout.tsx

import { Geist, Geist_Mono } from "next/font/google"; // Custom fonts
import "@styles/globals.css"; // Global CSS
import Footer from "@/components/Footer"; // Footer component
import Navbar from "@/components/Navbar"; // Navbar component
import { AuthProvider } from "@/context/AuthContext"; // ✅ Import the AuthProvider

// Set up the custom fonts with CSS variable names
const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

// RootLayout wraps the entire application
export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body className={`${geistSans.variable} ${geistMono.variable} antialiased`}>
        {/* ✅ Provide authentication state to the entire app */}
        <AuthProvider>
          <Navbar /> {/* Navbar will now have access to auth state */}
          {children} {/* This renders the current page */}
          <Footer /> {/* Footer stays at the bottom */}
        </AuthProvider>
      </body>
    </html>
  );
}
