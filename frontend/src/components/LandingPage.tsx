'use client';

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/context/AuthContext";
import Link from "next/link";
//import { FaLock } from "react-icons/fa";
import RotatingText from './RotatingText';
import Image from "next/image";
import GradientText from './GradientText'



export default function LandingPage() {
  const { isAuthenticated, authChecked } = useAuth();
  const router = useRouter();

  // Redirect if already logged in
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
          
          {/* LEFT SIDE (TEXT) */}
          <div className="max-w-xl mx-auto md:mx-0">

            <h1 className="text-4xl md:text-5xl font-bold text-gray-900 mb-6 flex flex-wrap items-center">
              <span className="mr-2">Email</span>

              <RotatingText
                texts={[
                  "Summary",
                  "Classification",
                  "Protection",
                  "Analysis",
                ]}
                className="text-purple-600"
                rotationInterval={2000}
                staggerDuration={0.03}
                splitBy="characters"
                auto
                loop
              />

              <span className="ml-[-3px]">Simplified Access</span>
            </h1>

            <p className="text-lg text-gray-700 mb-6">
              With all your email data in one powerful AI-driven platform, 
              SentinelIQ helps you detect spam, flag threats, and evaluate 
              sender trust in real-time. Say goodbye to clutter and hello to clarity.
            </p>

            <div className="flex space-x-4">
              {/* Primary hero button: purple background, 3D gradient border on hover */}
              <Link href="/signup" legacyBehavior>
                <a
                  aria-label="Get started free"
                  className="relative inline-block p-px rounded-xl group"
                >
                  {/* gradient border overlay (appears on hover) */}
                  <span
                    aria-hidden
                    className="absolute inset-0 rounded-xl bg-gradient-to-r from-purple-500 via-fuchsia-500 to-indigo-500 p-[2px] opacity-0 transition-opacity duration-400 group-hover:opacity-100"
                  />
                  {/* inner button */}
                  <span className="relative z-10 block px-6 py-3 rounded-xl bg-purple-600 text-white font-semibold shadow-[0_8px_20px_rgba(99,102,241,0.15)] transform-gpu transition-transform duration-300 ease-in-out group-hover:-translate-y-0.5 active:translate-y-0.5">
                    <div className="flex items-center space-x-2">
                      <span className="transition-all duration-500 group-hover:translate-x-1">Get started free</span>
                      <svg
                        className="w-5 h-5 transition-transform duration-500 group-hover:translate-x-1"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                        strokeWidth={2}
                        aria-hidden="true"
                      >
                        <path strokeLinecap="round" strokeLinejoin="round" d="M13 7l5 5m0 0l-5 5m5-5H6" />
                      </svg>
                    </div>
                  </span>
                </a>
              </Link>
            </div>
          </div>

            {/* RIGHT SIDE (PNG Image) */}
            <div className="flex items-center justify-center md:justify-end w-full md:w-auto mt-8 md:mt-0">
              <Image
                src="/c4185536-b389-4e86-93fa-ede2493b379a.webp"
                alt="Sentinel Shield"
                width={380}
                height={380}
                className="object-contain drop-shadow-lg"
              />
            </div>

        </div>
      </section>

      {/* Features */}
      <section className="py-20 px-6 max-w-6xl mx-auto">
        <GradientText
          className="text-3xl font-bold text-center mb-12"
          colors={[
    "#7C3AED", // purple (start)
    "#6366F1", // indigo
    "#4079FF", // blue
    "#A21CAF", // deeper fuchsia
    "#7C3AED"  // purple (end, same as start)
  ]}
          animationSpeed={3}
          showBorder={false}
        >
          SentinelIQ Features
        </GradientText>
 
        {/* custom grid wrapper provides perspective for 3D hover */}
        <div className="feature-grid grid md:grid-cols-3 gap-10">
          <div className="feature-card bg-white p-6 rounded-xl shadow-md text-center">
            <h3 className="text-xl font-semibold mb-2">🧠 Trust Scoring</h3>
            <p className="text-gray-600">
              Instantly evaluate sender reputation using real-time behavioral analysis.
            </p>
          </div>

          <div className="feature-card bg-white p-6 rounded-xl shadow-md text-center">
            <h3 className="text-xl font-semibold mb-2">🚨 Spam & Phishing Detection</h3>
            <p className="text-gray-600">
              Go beyond spam filters—understand why an email was flagged and how dangerous it is.
            </p>
          </div>

          <div className="feature-card bg-white p-6 rounded-xl shadow-md text-center">
            <h3 className="text-xl font-semibold mb-2">🔍 Intent Classification</h3>
            <p className="text-gray-600">
              Automatically detect the purpose of every email: sales, scam, personal, or legit.
            </p>
          </div>
        </div>

        {/* Scoped styles for the 3D hover effect */}
        <style jsx>{`
          .feature-grid {
            perspective: 1000px;
          }
          .feature-card {
            transform-style: preserve-3d;
            transform-origin: center bottom;
            will-change: transform, box-shadow;
            transition: transform 450ms cubic-bezier(0.2, 0.8, 0.2, 1), box-shadow 450ms cubic-bezier(0.2, 0.8, 0.2, 1);
          }
          .feature-card:hover {
            transform: translateY(-18px) rotateX(6deg) scale(1.035);
            box-shadow: 0 30px 60px rgba(99,102,241,0.14);
          }
          /* slightly lift inner content to emphasize 3D */
          .feature-card > :global(*) {
            transform: translateZ(10px);
          }
        `}</style>
      </section>

      {/* Call to Action */}
      <section className="bg-purple-600 text-white text-center py-16 px-6">
        <h2 className="text-3xl md:text-4xl font-bold mb-4">
          Ready to Take Back Control of Your Inbox?
        </h2>

        <p className="text-lg mb-8">
          Join SentinelIQ today and experience email security like never before.
        </p>

        {/* Secondary CTA: white inner background but keep 3D gradient border */}
        <Link href="/signup" legacyBehavior>
          <a
            aria-label="Start Protecting Now"
            className="relative inline-block p-px rounded-xl group"
          >
            <span
              aria-hidden
              className="absolute inset-0 rounded-xl bg-gradient-to-r from-purple-500 via-fuchsia-500 to-indigo-500 p-[2px] opacity-0 transition-opacity duration-400 group-hover:opacity-100"
            />
            <span className="relative z-10 block px-6 py-3 rounded-xl bg-white text-purple-700 font-semibold shadow-[0_8px_20px_rgba(0,0,0,0.12)] transform-gpu transition-transform duration-300 ease-in-out group-hover:-translate-y-0.5 active:translate-y-0.5">
              <div className="flex items-center justify-center space-x-2">
                <span className="transition-all duration-500 group-hover:translate-x-1">Start Protecting Now</span>
                <svg
                  className="w-5 h-5 transition-transform duration-500 group-hover:translate-x-1"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                  strokeWidth={2}
                  aria-hidden="true"
                >
                  <path strokeLinecap="round" strokeLinejoin="round" d="M13 7l5 5m0 0l-5 5m5-5H6" />
                </svg>
              </div>
            </span>
          </a>
        </Link>
      </section>
    </div>
  );
}
