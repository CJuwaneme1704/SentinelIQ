'use client';




export default function DashboardView() {
  interface Post {
    userId: string;
    content: string;
    trustScore: number;
  }

  const [posts, setPosts] = useState<Post[]>([]);
  const [loading, setLoading] = useState(true);

  // Fetch protected posts using JWT
  useEffect(() => {
    const token = localStorage.getItem('token');

    fetch('http://localhost:8080/api/protected-posts', {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((res) => {
        if (!res.ok) throw new Error('Unauthorized or failed to fetch posts');
        return res.json();
      })
      .then((data) => {
        setPosts(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error('Fetch error:', err);
        setLoading(false);
      });
  }, []);

  const pieData = {
    labels: ['Trusted Posts', 'Flagged Posts'],
    datasets: [
      {
        label: 'Post Distribution',
        data: [
          posts.filter((p) => p.trustScore > 50).length,
          posts.filter((p) => p.trustScore <= 50).length,
        ],
        backgroundColor: ['#4CAF50', '#F44336'],
        borderWidth: 1,
      },
    ],
  };

  const userStats = {
    totalUsers: 1200,
    activeUsers: 850,
    flaggedPosts: posts.filter((p) => p.trustScore <= 50).length,
  };

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
        {loading ? (
          <p>Loading posts...</p>
        ) : posts.length === 0 ? (
          <p>No posts available.</p>
        ) : (
          posts.map((post: Post, index: number) => (
            <PostCard
              key={index}
              userId={post.userId}
              content={post.content}
              trustScore={post.trustScore}
            />
          ))
        )}
      </div>
    </div>
  );
}
