'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { Inbox, ChevronLeft, ChevronRight, Plus } from 'lucide-react';

export default function DashboardView() {
  const router = useRouter();
  const [username, setUsername] = useState<string>("");
  const [selectedInbox, setSelectedInbox] = useState<string | null>(null);
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [newInbox, setNewInbox] = useState('');
  const [inboxes, setInboxes] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);

  // Fetch user info on mount
  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const res = await fetch("http://localhost:8080/api/me", {
          method: "GET",
          credentials: "include",
        });

        //console.log("Status from /api/me:", res.status);
        console.log(res);
        console.log("Status from /api/me:", res.status);


        if (!res.ok) {
          throw new Error("Authentication required");
        }

        const data = await res.json();
        setUsername(data.username);
        setInboxes(data.inboxes || []);
        setLoading(false);
      } catch (error) {
        console.error("Failed to fetch user data:", error);
        router.push("/login");
      }
    };

    fetchUserData();
  }, [router]);

  const handleAddInbox = () => {
    if (newInbox.trim() && !inboxes.includes(newInbox)) {
      setInboxes([newInbox, ...inboxes]);
      setSelectedInbox(newInbox);
      setNewInbox('');
      setShowModal(false);
    }
  };

  if (loading) {
    return <div className="flex justify-center items-center min-h-screen">Loading...</div>;
  }

  return (
    <div className="min-h-screen flex bg-gray-100 text-gray-800 p-4 gap-4">
      {/* Modal for Adding Inbox */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-30 flex items-center justify-center z-50">
          <div className="bg-white rounded-xl shadow-lg p-6 w-80">
            <h3 className="text-lg font-semibold mb-4 text-purple-700">Add New Inbox</h3>
            <input
              type="text"
              value={newInbox}
              onChange={(e) => setNewInbox(e.target.value)}
              placeholder="Inbox name"
              className="w-full border rounded-md px-3 py-2 mb-4 focus:outline-none focus:ring-2 focus:ring-purple-400"
              autoFocus
            />
            <div className="flex justify-end gap-2">
              <button
                onClick={() => setShowModal(false)}
                className="px-4 py-2 rounded-md bg-gray-200 text-gray-700 hover:bg-gray-300 transition"
              >
                Cancel
              </button>
              <button
                onClick={handleAddInbox}
                className="px-4 py-2 rounded-md bg-purple-600 text-white hover:bg-purple-700 transition"
              >
                Add
              </button>
            </div>
          </div>
        </div>
      )}

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
            {username}&#39;s Inboxes
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
          {selectedInbox ? `${selectedInbox} Inbox` : `${username}'s Inbox`}
        </h1>

        {/* Inbox Placeholder */}
        {!selectedInbox && (
          <div className="text-gray-500">
            Please add and select an inbox to begin.
          </div>
        )}
      </main>
    </div>
  );
}
