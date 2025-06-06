'use client';

import React from 'react';
import { useRouter } from 'next/navigation';
import EmailCard from './EmailCard';

type EmailCardProps = {
  id: number;            // Add id field for actual email ID
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
      {emails.map((email) => (
        <div
          key={email.id}
          onClick={() => router.push(`/user_pages/protected/emailviewer?emailId=${email.id}`)} // Use real email ID here
          className="cursor-pointer"
        >
          <EmailCard {...email} />
        </div>
      ))}
    </div>
  );
};

export default EmailList;
