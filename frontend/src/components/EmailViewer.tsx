'use client';

import { useState, useEffect } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { ArrowLeft, Loader } from 'lucide-react';

interface Email {
  subject: string;
  sender: string;
  date: string | number | Date;
  body: string;
  trustScore: number;
  intent: string;
  spam: boolean;
  recommendation: string;
  aiInsight: string;
}

export default function EmailViewer() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const emailId = searchParams.get('emailId'); // get email ID from query param
  const [email, setEmail] = useState<Email | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchEmail() {
      console.log('Fetching email ID:', emailId);
      if (!emailId) {
        console.warn('No email ID provided in query params.');
        setLoading(false);
        return;
      }

      try {
      const res = await fetch(`http://localhost:8080/api/gmail/emails/${emailId}`, {
        credentials: 'include',
      });

        console.log('Fetch status:', res.status);
        if (!res.ok) throw new Error(`Failed to fetch email: ${res.status}`);

        const data: Email = await res.json();
        console.log('Fetched email data:', data);
        setEmail(data);
      } catch (error) {
        console.error('Fetch error:', error);
        setEmail(null);
      } finally {
        setLoading(false);
      }
    }

    fetchEmail();
  }, [emailId]);

  if (loading) return <Loader className="animate-spin" />;
  if (!email) return <p>Email not found or error loading email.</p>;

  return (
    <div className="min-h-screen p-4 bg-gradient-to-br from-gray-50 to-purple-50 dark:from-gray-900 dark:to-black text-gray-800 dark:text-white">
      {/* Header */}
      <div className="flex items-center gap-4 mb-6">
        <button
          onClick={() => router.back()}
          className="flex items-center text-purple-700 dark:text-purple-300 hover:scale-105 transition-transform"
        >
          <ArrowLeft className="mr-2" /> Back
        </button>
        <h1 className="text-xl font-semibold">{email.subject}</h1>
      </div>

      {/* Metadata */}
      <div className="text-sm text-gray-600 dark:text-gray-300 mb-4">
        From: <strong>{email.sender}</strong> |{' '}
        <span>{new Date(email.date).toLocaleString()}</span>
      </div>

      {/* Email content and AI analysis */}
      <div className="grid md:grid-cols-3 gap-6">
        <div className="md:col-span-2 bg-white dark:bg-gray-800 rounded-xl shadow-lg p-6 border border-gray-200 dark:border-gray-700">
          <h2 className="text-md font-bold mb-4 text-purple-700 dark:text-purple-300">
            📬 Email Content
          </h2>
          <div className="whitespace-pre-wrap text-sm leading-relaxed">{email.body}</div>
        </div>

        <div className="bg-white/60 dark:bg-white/10 backdrop-blur-md border border-purple-100 dark:border-purple-700 rounded-xl shadow-xl p-6 flex flex-col md:flex-row justify-between items-center">
          <div className="md:w-1/2 space-y-3">
            <h2 className="text-md font-bold text-purple-800 dark:text-purple-300">
              🔎 SentinelIQ Analysis
            </h2>
            <p>
              🔒 <span className="font-medium">Trust Score:</span> {email.trustScore}%
            </p>
            <p>
              🎯 <span className="font-medium">Intent:</span> {email.intent}
            </p>
            <p>
              🚨 <span className="font-medium">Spam Risk:</span> {email.spam ? 'Yes' : 'No'}
            </p>
            <div>
              <p className="font-semibold">💡 Recommendation:</p>
              <p className="text-sm italic">{email.recommendation}</p>
            </div>
          </div>
          <div className="md:w-1/2 flex justify-center items-center">
            <div
              className={`text-[100px] font-bold ${
                email.trustScore < 50 || email.spam
                  ? 'text-red-600 animate-pulse'
                  : 'text-green-500'
              }`}
            >
              {email.trustScore < 50 || email.spam ? '🛑' : '👍'}
            </div>
          </div>
        </div>
      </div>

      <div className="mt-6 bg-white dark:bg-gray-800 rounded-xl shadow p-6 border border-gray-200 dark:border-gray-700">
        <h2 className="text-md font-bold mb-2 text-purple-700 dark:text-purple-300">🧠 AI Insight</h2>
        <p className="text-sm">{email.aiInsight}</p>
      </div>
    </div>
  );
}
