interface PostCardProps {
    userId: string;
    content: string;
    trustScore: number;
  }
  
  export default function PostCard({ userId, content, trustScore }: PostCardProps) {
    return (
      <div className="border rounded-lg p-4 shadow-sm bg-white">
        <div className="text-sm text-gray-500 mb-1">User: {userId}</div>
        <p className="text-gray-800 mb-2">{content}</p>
        <div
          className={`text-sm font-semibold ${
            trustScore > 70 ? 'text-green-600' : trustScore > 40 ? 'text-yellow-500' : 'text-red-600'
          }`}
        >
          Trust Score: {trustScore}%
        </div>
      </div>
    );
  }
  