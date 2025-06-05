'use client';

import React from 'react';
import { useRouter } from 'next/navigation';
import EmailCard from './EmailCard';

type EmailCardProps = {
  sender: string;
  subject: string;
  trustScore: number;
  intent: string;
  spam: boolean;
};

type EmailListProps = {
  emails: EmailCardProps[];
};

const EmailList: React.FC<EmailListProps> = ({ emails }) => {
  const router = useRouter();

  if (emails.length === 0) {
    return (
      <div className="text-gray-500 py-8 text-center">
        📭 No emails to display.
      </div>
    );
  }

  return (
    <div className="space-y-3">
      {emails.map((email, index) => (
        <div
          key={index}
          onClick={() => router.push(`/user_pages/protected/emailviewer?emailIndex=${index}`)}
          className="cursor-pointer"
        >
          <EmailCard {...email} />
        </div>
      ))}
    </div>
  );
};

export default EmailList;
