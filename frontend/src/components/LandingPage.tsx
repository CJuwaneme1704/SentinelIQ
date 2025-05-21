'use client';

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/context/AuthContext";
import Link from "next/link";
import { FaLock } from "react-icons/fa";

export default function LandingPage() {
  const { isAuthenticated, authChecked } = useAuth();
  const router = useRouter();

  // 🔁 Auto-redirect if session is active
  useEffect(() => {
    if (authChecked && isAuthenticated) {
      router.replace("/user_pages/protected/dashboard");
    }
  }, [authChecked, isAuthenticated]);

  if (!authChecked || isAuthenticated) return null;

  return (
    <div className="min-h-screen bg-[#F9F9F9] text-gray-900 font-sans">
      {/* Hero Section */}
      <section className="w-full bg-[#EDEDED] px-8 py-16">
        <div className="max-w-7xl mx-auto flex flex-col-reverse md:flex-row justify-between items-center">
          <div className="max-w-xl mx-auto md:mx-0">
            <h1 className="text-4xl md:text-5xl font-bold text-gray-900 mb-6">
              Introducing Your New <br /> Inbox Defense Powerhouse
            </h1>
            <p className="text-lg text-gray-700 mb-6">
              With all your email data in one powerful AI-driven platform, SentinelIQ helps you detect spam, flag threats, and evaluate sender trust in real-time. Say goodbye to clutter and hello to clarity.
            </p>
            <div className="flex space-x-4">
              <Link
                href="/signup"
                className="bg-purple-600 hover:bg-purple-700 focus:ring-4 focus:ring-purple-300 text-white font-semibold py-3 px-6 rounded transition duration-300"
              >
                Get started free
              </Link>
            </div>
          </div>

          <div className="mb-10 md:mb-0 md:-ml-6">
            <div className="relative w-[300px] h-[300px] rounded-full overflow-hidden bg-purple-600 flex items-center justify-center">
              <FaLock className="text-white text-8xl" />
            </div>
          </div>
        </div>
      </section>

      {/* Features */}
      <section className="py-20 px-6 max-w-6xl mx-auto">
        <h2 className="text-3xl font-bold text-center mb-12">SentinelIQ Features</h2>
        <div className="grid md:grid-cols-3 gap-10">
          <div className="bg-white p-6 rounded shadow text-center">
            <h3 className="text-xl font-semibold mb-2">🧠 Trust Scoring</h3>
            <p className="text-gray-600">
              Instantly evaluate sender reputation using real-time behavioral analysis.
            </p>
          </div>
          <div className="bg-white p-6 rounded shadow text-center">
            <h3 className="text-xl font-semibold mb-2">🚨 Spam & Phishing Detection</h3>
            <p className="text-gray-600">
              Go beyond spam filters—understand why an email was flagged and how dangerous it is.
            </p>
          </div>
          <div className="bg-white p-6 rounded shadow text-center">
            <h3 className="text-xl font-semibold mb-2">🔍 Intent Classification</h3>
            <p className="text-gray-600">
              Automatically detect the purpose of every email: sales, scam, personal, or legit.
            </p>
          </div>
        </div>
      </section>

      {/* Call to Action */}
      <section className="bg-purple-600 text-white text-center py-16 px-6">
        <h2 className="text-3xl md:text-4xl font-bold mb-4">
          Ready to Take Back Control of Your Inbox?
        </h2>
        <p className="text-lg mb-8">
          Join SentinelIQ today and experience email security like never before.
        </p>
        <Link
          href="/signup"
          className="bg-white text-purple-700 hover:bg-gray-100 focus:ring-4 focus:ring-purple-300 font-semibold py-3 px-6 rounded shadow transition duration-300"
        >
          Start Protecting Now
        </Link>
      </section>
    </div>
  );
}
