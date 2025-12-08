"use client";

import React from "react";
import Link from "next/link";

interface Props {
  href?: string;
  children: React.ReactNode;
  className?: string;
  onClick?: () => void;
}

export default function FancyButton({ href, children, className = "", onClick }: Props) {
  const inner = (
    <span className="relative inline-block p-px font-semibold leading-6 text-white cursor-pointer rounded-xl transition-transform duration-300 ease-in-out hover:scale-105 active:scale-95">
      
      {/* Outer gradient border */}
      <span className="absolute inset-0 rounded-xl bg-gradient-to-r from-teal-400 via-blue-500 to-purple-500 p-[2px] opacity-0 transition-opacity duration-500 group-hover:opacity-100"></span>

      {/* Inner block keeps button’s original color */}
      <span
        className={`relative z-10 block px-6 py-3 rounded-xl ${className}`}
      >
        <div className="relative z-10 flex items-center space-x-2">
          {children}
        </div>
      </span>
    </span>
  );

  if (href) {
    return (
      <Link href={href} className="group">
        {inner}
      </Link>
    );
  }

  return (
    <button className="group" onClick={onClick}>
      {inner}
    </button>
  );
}
