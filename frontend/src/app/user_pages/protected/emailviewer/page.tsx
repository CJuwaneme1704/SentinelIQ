'use client';

import { useEffect, useState } from 'react';
import { useSearchParams } from 'next/navigation';
import EmailCard from '@/components/EmailCard';

interface Email {
  id: number;
  sender: string;
  subject: string;
  body: string;
  receivedAt: string;
  isSpam: boolean;
  trustScore: number;
}

export default function EmailViewerPage() {
  const searchParams = useSearchParams();
  const accountId = searchParams.get('accountId');
  const [emails, setEmails] = useState<Email[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchEmails = async () => {
      if (!accountId) return;
      try {
        const res = await fetch(
          `http://localhost:8080/api/emailAccounts/${accountId}/emails`,
          { credentials: 'include' }
        );
        if (res.ok) {
          const data = await res.json();
          setEmails(data);
        }
      } catch (err) {
        console.error('Failed to fetch emails', err);
      } finally {
        setLoading(false);
      }
    };

    fetchEmails();
  }, [accountId]);

  if (!accountId) {
    return <div className="p-4">No inbox selected.</div>;
  }

  if (loading) {
    return <div className="p-4">Loading...</div>;
  }

  return (
    <div className="p-4 space-y-3">
      {emails.map((email) => (
        <EmailCard
          key={email.id}
          sender={email.sender}
          subject={email.subject}
          trustScore={email.trustScore}
          intent="Unknown"
          spam={email.isSpam}
        />
      ))}
    </div>
  );
}
