import React from "react";

type EmailCardProps = {
  sender: string;
  subject: string;
  trustScore: number;
  intent: string;
  spam: boolean;
  onClick?: () => void;
};

const EmailCard: React.FC<EmailCardProps> = ({
  sender,
  subject,
  trustScore,
  intent,
  spam,
  onClick,
}) => {
  return (
    <div
      className={`rounded-xl border shadow-sm p-4 mb-3 bg-white cursor-pointer hover:shadow-lg transition ${
        spam ? "opacity-70" : ""
      }`}
      onClick={onClick}
    >
      <div className="flex justify-between items-center mb-2">
        <span className="font-semibold text-purple-700">{sender}</span>
        <span
          className={`px-2 py-1 rounded-full text-xs font-medium text-white ${
            trustScore > 70
              ? "bg-green-500"
              : trustScore > 40
              ? "bg-yellow-500"
              : "bg-red-500"
          }`}
        >
          {trustScore}%
        </span>
      </div>
      <div className="mb-1 text-gray-800 font-medium">{subject}</div>
      <div className="flex items-center gap-4 text-xs">
        <span
          className={`px-2 py-1 rounded bg-gray-100 text-gray-700 font-semibold`}
        >
          {intent}
        </span>
        {spam ? (
          <span className="px-2 py-1 rounded bg-red-100 text-red-700 font-semibold">
            Spam
          </span>
        ) : (
          <span className="px-2 py-1 rounded bg-green-100 text-green-700 font-semibold">
            Not Spam
          </span>
        )}
      </div>
    </div>
  );
};

export default EmailCard;