'use client';

import { useState } from 'react';
import {
  Inbox,
  ChevronLeft,
  ChevronRight,
  Plus,
} from 'lucide-react';

export default function DashboardView() {
  const [selectedInbox, setSelectedInbox] = useState<string | null>(null);
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [newInbox, setNewInbox] = useState('');
  const [inboxes, setInboxes] = useState<string[]>([]);

  const [emails] = useState([
    {
      id: 1,
      accountType: 'Gmail',
      sender: 'john.doe@gmail.com',
      subject: 'Meeting Reminder',
      trustScore: 85,
      intent: 'Informative',
      spam: false,
    },
    {
      id: 2,
      accountType: 'Yahoo',
      sender: 'unknown@phishing.com',
      subject: 'Urgent: Update Your Account',
      trustScore: 20,
      intent: 'Fraudulent',
      spam: true,
    },
    {
      id: 3,
      accountType: 'Outlook',
      sender: 'newsletter@company.com',
      subject: 'Weekly Updates',
      trustScore: 70,
      intent: 'Promotional',
      spam: false,
    },
    {
      id: 4,
      accountType: 'Gmail',
      sender: 'alerts@bank.com',
      subject: 'Security Alert',
      trustScore: 30,
      intent: 'Security',
      spam: true,
    },
  ]);

  const filteredEmails = emails.filter(
    (email) => email.accountType === selectedInbox
  );

  const handleAddInbox = () => {
    if (newInbox.trim() && !inboxes.includes(newInbox)) {
      setInboxes([newInbox, ...inboxes]);
      setSelectedInbox(newInbox);
      setNewInbox('');
      setShowModal(false);
    }
  };

  return (
    <div className="min-h-screen flex bg-gray-100 text-gray-800 p-4 gap-4">
      {/* Sidebar */}
      <aside
        className={`transition-all duration-300 bg-white border border-white shadow-xl rounded-2xl p-4 flex flex-col ${
          sidebarOpen ? 'w-64' : 'w-20 items-center'
        }`}
        style={{ maxHeight: '90vh' }}
      >
        {/* Toggle Button */}
        <button
          className="mb-4 self-end text-purple-600 hover:text-purple-800 transition"
          onClick={() => setSidebarOpen(!sidebarOpen)}
        >
          {sidebarOpen ? <ChevronLeft size={20} /> : <ChevronRight size={20} />}
        </button>

        {/* Title */}
        {sidebarOpen && (
          <h2 className="text-lg font-semibold text-purple-700 mb-2">
            Inbox Accounts
          </h2>
        )}

        {/* Add Inbox Button */}
        <button
          onClick={() => setShowModal(true)}
          className="mb-4 flex items-center gap-2 bg-purple-100 text-purple-800 px-4 py-2 rounded-lg hover:bg-purple-200 transition w-full"
        >
          <Plus size={18} />
          {sidebarOpen && <span>Add Inbox</span>}
        </button>

        {/* Inbox List */}
        <ul className="flex-1 space-y-2 w-full overflow-y-auto">
          {inboxes.map((inbox) => (
            <li
              key={inbox}
              onClick={() => setSelectedInbox(inbox)}
              className={`flex items-center gap-2 px-4 py-2 rounded-md cursor-pointer transition ${
                selectedInbox === inbox
                  ? 'bg-purple-600 text-white'
                  : 'hover:bg-purple-100'
              }`}
            >
              <Inbox size={16} />
              {sidebarOpen && <span>{inbox}</span>}
            </li>
          ))}
        </ul>
      </aside>

      {/* Main Content */}
      <main className="flex-1 p-4">
        <h1 className="text-3xl font-bold text-purple-700 mb-6">
          {selectedInbox ? `${selectedInbox} Inbox` : 'No Inbox Selected'}
        </h1>

        {/* Email Table */}
        {selectedInbox ? (
          <div className="bg-white rounded-xl shadow-lg overflow-hidden border">
            <table className="min-w-full table-auto text-sm">
              <thead className="bg-purple-600 text-white">
                <tr>
                  <th className="px-4 py-3 text-left">Sender</th>
                  <th className="px-4 py-3 text-left">Subject</th>
                  <th className="px-4 py-3 text-center">Trust Score</th>
                  <th className="px-4 py-3 text-center">Intent</th>
                  <th className="px-4 py-3 text-center">Spam</th>
                </tr>
              </thead>
              <tbody>
                {filteredEmails.map((email) => (
                  <tr
                    key={email.id}
                    className="border-b hover:bg-gray-50 transition"
                  >
                    <td className="px-4 py-3">{email.sender}</td>
                    <td className="px-4 py-3">{email.subject}</td>
                    <td className="px-4 py-3 text-center">
                      <span
                        className={`px-2 py-1 rounded-full text-white text-xs font-medium ${
                          email.trustScore > 70
                            ? 'bg-green-500'
                            : email.trustScore > 40
                            ? 'bg-yellow-500'
                            : 'bg-red-500'
                        }`}
                      >
                        {email.trustScore}%
                      </span>
                    </td>
                    <td className="px-4 py-3 text-center">{email.intent}</td>
                    <td className="px-4 py-3 text-center">
                      {email.spam ? (
                        <span className="text-red-600 font-semibold">Yes</span>
                      ) : (
                        <span className="text-green-600 font-semibold">No</span>
                      )}
                    </td>
                  </tr>
                ))}
                {filteredEmails.length === 0 && (
                  <tr>
                    <td
                      colSpan={5}
                      className="text-center px-4 py-6 text-gray-500"
                    >
                      No emails found in this inbox.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="text-gray-500">
            Please add and select an inbox to begin.
          </div>
        )}
      </main>

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg shadow-xl p-6 w-full max-w-sm space-y-4">
            <h3 className="text-xl font-bold text-purple-700">
              Register New Inbox
            </h3>
            <input
              type="text"
              placeholder="Inbox Name (e.g. Zoho)"
              className="w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-purple-600"
              value={newInbox}
              onChange={(e) => setNewInbox(e.target.value)}
            />
            <div className="flex justify-end gap-2">
              <button
                onClick={() => setShowModal(false)}
                className="px-4 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300"
              >
                Cancel
              </button>
              <button
                onClick={handleAddInbox}
                className="px-4 py-2 bg-purple-600 text-white rounded hover:bg-purple-700"
              >
                Add
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
