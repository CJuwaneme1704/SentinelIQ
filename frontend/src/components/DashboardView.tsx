'use client'; // Next.js directive for client-side rendering

import { useState, useEffect } from 'react'; // React hooks for state and lifecycle
import { useRouter, useSearchParams } from 'next/navigation'; // Next.js hooks for routing and search params
import { Inbox, ChevronLeft, ChevronRight, Plus, Trash2 } from 'lucide-react'; // Icon components
import EmailList from '@/components/EmailList'; // Email list component

// Type for the email card props
type EmailCardProps = {
  sender: string;
  subject: string;
  trustScore: number;
  intent: string;
  spam: boolean;
};

// Type for an inbox object
type Inbox = {
  id: number;
  displayName: string;
  emailAddress: string;
  provider: string;
  isPrimary: boolean;
};

export default function DashboardView() {
  const router = useRouter(); // Router instance for navigation
  const searchParams = useSearchParams(); // Get URL search params
  const refresh = searchParams.get('refresh'); // Get 'refresh' param if present

  // State variables for user, inboxes, UI, etc.
  const [username, setUsername] = useState(''); // Username of the logged-in user
  const [selectedInbox, setSelectedInbox] = useState<Inbox | null>(null); // Currently selected inbox
  const [sidebarOpen, setSidebarOpen] = useState(true); // Sidebar open/close state
  const [showModal, setShowModal] = useState(false); // Show/hide the add inbox modal
  const [inboxes, setInboxes] = useState<Inbox[]>([]); // List of inboxes
  const [emails, setEmails] = useState<EmailCardProps[]>([]); // List of emails for the selected inbox
  const [loading, setLoading] = useState(true); // Loading state for data fetch
  const [inboxToDelete, setInboxToDelete] = useState<Inbox | null>(null); // Inbox pending deletion

  const DEV_MODE = process.env.NEXT_PUBLIC_DEV_MODE === 'true'; // Development mode flag

  // Fetch user data or use dev mode data on mount or when refresh/DEV_MODE/router changes
  useEffect(() => {
    if (DEV_MODE) {
      // In dev mode, use hardcoded user and inbox data
      setUsername('Jerome');
      setInboxes([
        {
          id: 1,
          displayName: "Dev Inbox",
          emailAddress: "dev@example.com",
          provider: "GMAIL",
          isPrimary: false,
        }
      ]);
      setEmails([]); // No emails in dev mode by default
      setLoading(false); // Data is ready
      return;
    }

    // Otherwise, fetch user data from backend API
    const fetchUserData = async () => {
      try {
        const res = await fetch('http://localhost:8080/api/me', {
          method: 'GET',
          credentials: 'include', // Send cookies for authentication
        });

        if (!res.ok) throw new Error('Authentication required'); // Redirect if not authenticated

        const data = await res.json();
        setUsername(data.username); // Set username from API
        setInboxes(data.inboxes || []); // Set inboxes from API

        // Fetch emails for the first inbox if available
        if (data.inboxes && data.inboxes.length > 0) {
          const firstInbox = data.inboxes[0];
          setSelectedInbox(firstInbox);

          // Fetch emails for the selected inbox using its id
          const emailRes = await fetch(`http://localhost:8080/api/gmail/emails?inboxId=${firstInbox.id}`, {
            credentials: 'include',
          });

          if (emailRes.ok) {
            const emailData = await emailRes.json();
            setEmails(emailData); // Set emails from API response
          } else {
            setEmails([]); // If fetch fails, set empty emails
          }
        } else {
          setSelectedInbox(null);
          setEmails([]);
        }

        setLoading(false); // Data is ready
      } catch (error) {
        console.error('Failed to fetch user data:', error);
        router.push('/login'); // Redirect to login on error
      }
    };

    fetchUserData(); // Call the fetch function
  }, [refresh, DEV_MODE, router]);

  // Handler for Gmail inbox linking
  const handleGmailInbox = () => {
    window.location.href = 'http://localhost:8080/auth/gmail';
  };

  // Handler for Yahoo inbox linking
  const handleYahooInbox = () => {
    window.location.href = 'http://localhost:8080/auth/yahoo';
  };

  // Handler for Outlook inbox linking
  const handleOutlookInbox = () => {
    window.location.href = 'http://localhost:8080/auth/outlook';
  };

  // Handler for deleting an inbox
  const handleDeleteInbox = async (inbox: Inbox) => {
    // Placeholder: implement real API delete if needed
    setInboxes(inboxes.filter((i) => i.id !== inbox.id)); // Remove inbox from list
    if (selectedInbox?.id === inbox.id) setSelectedInbox(null); // Deselect if deleted
    setInboxToDelete(null); // Close modal
  };

  // Show loading spinner while fetching data
  if (loading) {
    return <div className="flex justify-center items-center min-h-screen">Loading...</div>;
  }

  return (
    <div className="min-h-screen flex bg-gray-100 text-gray-800 p-4 gap-4">
      {/* Inbox Modal for linking new inbox */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-30 flex items-center justify-center z-50">
          <div className="bg-white rounded-xl shadow-lg p-6 w-96">
            <h3 className="text-lg font-semibold mb-4 text-purple-700">Link an Inbox</h3>
            <p className="text-sm text-gray-600 mb-4">Choose an email provider to link:</p>
            <div className="space-y-3">
              {/* Gmail link button */}
              <button
                onClick={handleGmailInbox}
                className="w-full py-2 bg-red-500 text-white rounded hover:bg-red-600"
              >
                Link Gmail
              </button>
              {/* Yahoo link button */}
              <button
                onClick={handleYahooInbox}
                className="w-full py-2 bg-purple-500 text-white rounded hover:bg-purple-600"
              >
                Link Yahoo
              </button>
              {/* Outlook link button */}
              <button
                onClick={handleOutlookInbox}
                className="w-full py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
              >
                Link Outlook
              </button>
            </div>
            {/* Cancel button for modal */}
            <div className="flex justify-end mt-6">
              <button
                onClick={() => setShowModal(false)}
                className="px-4 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300"
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Delete Modal for confirming inbox deletion */}
      {inboxToDelete && (
        <div className="fixed inset-0 bg-black bg-opacity-30 flex items-center justify-center z-50">
          <div className="bg-white rounded-xl shadow-lg p-6 w-96">
            <h3 className="text-lg font-semibold mb-4 text-red-700">Delete Inbox</h3>
            <p className="mb-4">Are you sure you want to delete <strong>{inboxToDelete.displayName}</strong>?</p>
            <div className="flex justify-end gap-2">
              {/* Cancel deletion */}
              <button onClick={() => setInboxToDelete(null)} className="px-4 py-2 bg-gray-200 rounded hover:bg-gray-300">Cancel</button>
              {/* Confirm deletion */}
              <button onClick={() => handleDeleteInbox(inboxToDelete)} className="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700">Delete</button>
            </div>
          </div>
        </div>
      )}

      {/* Sidebar for inbox navigation */}
      <aside
        className={`transition-all bg-white border border-white rounded-2xl p-4 flex flex-col shadow-xl ${sidebarOpen ? 'w-64' : 'w-20 items-center'}`}
        style={{ maxHeight: '90vh' }}
      >
        {/* Sidebar toggle chevron */}
        <div
          className={`mb-4 w-full ${
            sidebarOpen
              ? 'flex justify-end'
              : 'flex justify-center' // Only horizontal centering when minimized
          }`}
        >
          <button onClick={() => setSidebarOpen(!sidebarOpen)} className="text-purple-600 hover:text-purple-800">
            {sidebarOpen ? <ChevronLeft size={20} /> : <ChevronRight size={20} />}
          </button>
        </div>

        {/* Username and Add Inbox button (only when sidebar is open) */}
        {sidebarOpen && (
          <>
            <h2 className="text-lg font-semibold text-purple-700 mb-2">{username}&#39;s Inboxes</h2>
            <button onClick={() => setShowModal(true)} className="mb-4 flex items-center gap-2 bg-purple-100 text-purple-800 px-4 py-2 rounded hover:bg-purple-200">
              <Plus size={18} />
              <span>Add Inbox</span>
            </button>
          </>
        )}

        {/* List of inboxes */}
        <ul className="space-y-2 w-full overflow-y-auto flex-1">
          {inboxes.map((inbox) => (
           <li
            key={inbox.id}
            className={`flex items-center px-4 py-2 rounded cursor-pointer transition justify-between ${
              selectedInbox?.id === inbox.id ? 'bg-purple-600 text-white' : 'hover:bg-purple-100'
            }`}
          >
            {/* Inbox icon and name */}
            <span
              className="flex items-center gap-2 overflow-hidden flex-1"
              onClick={() => setSelectedInbox(inbox)}
            >
              <Inbox size={16} className="flex-shrink-0" />
              {sidebarOpen && (
                <span className="truncate max-w-[150px]" title={inbox.displayName}>
                  {inbox.displayName}
                </span>
              )}
            </span>

            {/* Delete inbox button (only when sidebar is open) */}
            {sidebarOpen && (
              <button
                onClick={() => setInboxToDelete(inbox)}
                className="ml-2 text-red-400 hover:text-red-700 flex-shrink-0"
                title="Delete inbox"
              >
                <Trash2 size={16} />
              </button>
            )}
          </li>
          ))}
        </ul>
      </aside>

      {/* Main Email Viewer */}
      <main className="flex-1 p-4">
        {selectedInbox ? (
          <EmailList emails={emails} /> // Show emails for selected inbox
        ) : (
          <div className="text-gray-500">Please add and select an inbox to begin.</div> // Prompt if no inbox selected
        )}
      </main>
    </div>
  );
}
