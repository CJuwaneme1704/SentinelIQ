import React, { ReactNode } from 'react';

interface GradientTextProps {
  children: ReactNode;
  className?: string;
  colors?: string[];
  animationSpeed?: number;
  showBorder?: boolean;
}

export default function GradientText({
  children,
  className = '',
    colors = [
    "#7C3AED", // purple (start)
    "#6366F1", // indigo
    "#4079FF", // blue
    "#40FFAA", // cyan/green accent
    "#A21CAF", // deeper fuchsia
    "#7C3AED"  // purple (end, same as start)
  ],
  animationSpeed = 3,
  showBorder = false
}: GradientTextProps) {

  const gradient = `linear-gradient(90deg, ${colors.join(', ')})`;

  return (
    <div className={`relative ${className}`}>
      {/* KEYFRAMES (NO TAILWIND CONFIG NEEDED) */}
      <style jsx>{`
        @keyframes gradientMove {
          0% { background-position: 0% 50%; }
          50% { background-position: 100% 50%; }
          100% { background-position: 0% 50%; }
        }
      `}</style>

      <span
        className="inline-block"
        style={{
          backgroundImage: gradient,
          backgroundSize: "300% 300%",
          backgroundClip: "text",
          WebkitBackgroundClip: "text",
          color: "transparent",
          animation: `gradientMove ${animationSpeed}s ease infinite`,
        }}
      >
        {children}
      </span>
    </div>
  );
}
