'use client';

import PostCard from './PostCard';
import { Pie } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend,
} from 'chart.js';

ChartJS.register(ArcElement, Tooltip, Legend);

const mockPosts = [
  {
    userId: 'jerome123',
    content: 'This is definitely not spam 🤞',
    trustScore: 88,
  },
  {
    userId: 'troll_user88',
    content: 'FREE MONEY!!! Click now!!!',
    trustScore: 12,
  },
  {
    userId: 'ai_babe99',
    content: 'What do you think about AI moderation tools?',
    trustScore: 95,
  },
];

const userStats = {
  totalUsers: 1200,
  activeUsers: 850,
  flaggedPosts: 45,
};

const pieData = {
  labels: ['Trusted Posts', 'Flagged Posts'],
  datasets: [
    {
      label: 'Post Distribution',
      data: [mockPosts.filter(p => p.trustScore > 50).length, mockPosts.filter(p => p.trustScore <= 50).length],
      backgroundColor: ['#4CAF50', '#F44336'],
      borderWidth: 1,
    },
  ],
};

export default function DashboardView() {
  return (
    <div className="p-6 max-w-4xl mx-auto">
      <h1 className="text-3xl font-bold mb-6">🛡️ SentinelIQ Dashboard</h1>

      {/* Stats Section */}
      <div className="grid grid-cols-3 gap-4 mb-6">
        <div className="p-4 bg-gray-100 rounded shadow">
          <h2 className="text-xl font-semibold">Total Users</h2>
          <p className="text-2xl font-bold">{userStats.totalUsers}</p>
        </div>
        <div className="p-4 bg-gray-100 rounded shadow">
          <h2 className="text-xl font-semibold">Active Users</h2>
          <p className="text-2xl font-bold">{userStats.activeUsers}</p>
        </div>
        <div className="p-4 bg-gray-100 rounded shadow">
          <h2 className="text-xl font-semibold">Flagged Posts</h2>
          <p className="text-2xl font-bold">{userStats.flaggedPosts}</p>
        </div>
      </div>

      {/* Pie Chart Section */}
      <div className="mb-6">
        <h2 className="text-2xl font-bold mb-4">Post Trust Distribution</h2>
        <Pie data={pieData} />
      </div>

      {/* Posts Section */}
      <div className="space-y-4">
        {mockPosts.map((post, index) => (
          <PostCard
            key={index}
            userId={post.userId}
            content={post.content}
            trustScore={post.trustScore}
          />
        ))}
      </div>
    </div>
  );
}
