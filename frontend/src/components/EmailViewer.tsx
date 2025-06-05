'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
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

export default function EmailViewer({ email }: { email?: Email }) {
  const router = useRouter();
  const [prompt, setPrompt] = useState('');
  const [response, setResponse] = useState('');
  const [streamingText, setStreamingText] = useState('');
  const [loading, setLoading] = useState(false);

  const mockEmail: Email = {
    subject: 'Missing Email',
    sender: 'unknown@example.com',
    date: new Date(),
    body: 'No email data available.',
    trustScore: 0,
    intent: 'Unknown',
    spam: false,
    recommendation: 'Unable to generate recommendation.',
    aiInsight: 'Email data was not provided or failed to load.',
  };

  const actualEmail = email ?? mockEmail;

  const isDangerous =
    actualEmail.trustScore < 50 || actualEmail.intent === 'Phishing' || actualEmail.spam;

  const handlePromptSubmit = async () => {
    if (!prompt.trim()) return;
    setLoading(true);
    setResponse('');
    setStreamingText('');

    try {
      const res = await fetch('http://localhost:8080/api/ai/prompt', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ prompt, emailBody: actualEmail.body }),
      });

      const data = await res.json();
      const reply = data.reply || 'No response';

      let i = 0;
      const interval = setInterval(() => {
        setStreamingText((prev) => prev + reply[i]);
        i++;
        if (i >= reply.length) {
          clearInterval(interval);
          setLoading(false);
        }
      }, 15);
    } catch (error) {
      console.error('AI prompt error:', error);
      setResponse('Error fetching response.');
      setLoading(false);
    }
  };

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
        <h1 className="text-xl font-semibold">{actualEmail.subject}</h1>
      </div>

      {/* Metadata */}
      <div className="text-sm text-gray-600 dark:text-gray-300 mb-4">
        From: <strong>{actualEmail.sender}</strong> |{' '}
        <span>{new Date(actualEmail.date).toLocaleString()}</span>
      </div>

      {/* Main Grid */}
      <div className="grid md:grid-cols-3 gap-6">
        {/* Email Content */}
        <div className="md:col-span-2 bg-white dark:bg-gray-800 rounded-xl shadow-lg p-6 border border-gray-200 dark:border-gray-700">
          <h2 className="text-md font-bold mb-4 text-purple-700 dark:text-purple-300">📬 Email Content</h2>
          <div className="whitespace-pre-wrap text-sm leading-relaxed">{actualEmail.body}</div>
        </div>

        {/* AI Insights with Icon */}
        <div className="bg-white/60 dark:bg-white/10 backdrop-blur-md border border-purple-100 dark:border-purple-700 rounded-xl shadow-xl p-6 flex flex-col md:flex-row justify-between items-center">
          <div className="md:w-1/2 space-y-3">
            <h2 className="text-md font-bold text-purple-800 dark:text-purple-300">🔎 SentinelIQ Analysis</h2>
            <p>🔒 <span className="font-medium">Trust Score:</span> {actualEmail.trustScore}%</p>
            <p>🎯 <span className="font-medium">Intent:</span> {actualEmail.intent}</p>
            <p>🚨 <span className="font-medium">Spam Risk:</span> {actualEmail.spam ? 'Yes' : 'No'}</p>
            <div>
              <p className="font-semibold">💡 Recommendation:</p>
              <p className="text-sm italic">{actualEmail.recommendation}</p>
            </div>
          </div>
          <div className="md:w-1/2 flex justify-center items-center">
            <div className={`text-[100px] font-bold ${isDangerous ? 'text-red-600 animate-pulse' : 'text-green-500'}`}>
              {isDangerous ? '🛑' : '👍'}
            </div>
          </div>
        </div>
      </div>

      {/* AI Insight */}
      <div className="mt-6 bg-white dark:bg-gray-800 rounded-xl shadow p-6 border border-gray-200 dark:border-gray-700">
        <h2 className="text-md font-bold mb-2 text-purple-700 dark:text-purple-300">🧠 AI Insight</h2>
        <p className="text-sm">{actualEmail.aiInsight}</p>
      </div>

      {/* Ask AI */}
      <div className="mt-6 bg-white dark:bg-gray-800 border border-purple-200 dark:border-purple-700 shadow-xl rounded-xl p-6">
        <h2 className="text-md font-bold mb-4 text-purple-700 dark:text-purple-300">💬 Ask SentinelIQ</h2>
        <div className="flex flex-col md:flex-row gap-4">
          <input
            type="text"
            placeholder="e.g., Is this safe to respond to?"
            value={prompt}
            onChange={(e) => setPrompt(e.target.value)}
            className="flex-1 px-4 py-2 border rounded-lg border-purple-300 shadow-sm focus:outline-none focus:ring-2 focus:ring-purple-500 dark:bg-gray-900"
          />
          <button
            onClick={handlePromptSubmit}
            className="px-6 py-2 bg-gradient-to-r from-purple-600 to-fuchsia-600 text-white rounded-md shadow-md hover:scale-105 transition"
          >
            Ask
          </button>
        </div>

        {/* AI Response */}
        <div className="mt-4 text-sm whitespace-pre-wrap min-h-[80px] transition-all">
          {loading ? (
            <div className="flex items-center gap-2 text-purple-400 animate-pulse">
              <Loader className="animate-spin" /> SentinelIQ is thinking...
            </div>
          ) : (
            <div className="bg-gray-100 dark:bg-gray-900 border border-gray-200 dark:border-gray-700 p-4 rounded-lg shadow-sm">
              {streamingText || response}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
