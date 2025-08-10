'use client';

import React from 'react';
import { useRouter } from 'next/navigation';
import EmailCard from './EmailCard';

type EmailCardProps = {
  id: number;
  sender: string;
  subject: string;
  trustScore: number;
  intent: string;
  spam: boolean;
};

type EmailListProps = {
  emails: EmailCardProps[];
  onEmailSelect?: (email: EmailCardProps) => void;
  onResync: () => void;
};

const EmailList: React.FC<EmailListProps> = ({ emails, onEmailSelect, onResync }) => {
  const router = useRouter();

  return (
    <div className="space-y-3">
      <button
        onClick={onResync}
        className="px-4 py-2 bg-purple-600 text-white rounded hover:bg-purple-700 mb-4"
      >
        🔄 Resync Inbox
      </button>

      {emails.length === 0 ? (
        <div className="text-gray-500 py-8 text-center">📭 No emails to display.</div>
      ) : (
        emails.map((email) => (
          <div
            key={email.id}
            onClick={() =>
              onEmailSelect
                ? onEmailSelect(email)
                : router.push(`/user_pages/protected/emailviewer?emailId=${email.id}`)
            }
            className="cursor-pointer"
          >
            <EmailCard {...email} />
          </div>
        ))
      )}
    </div>
  );
};

export default EmailList;
