export default function Footer() {
  return (
    <footer className="bg-[#EDEDED] text-gray-700 py-8">
      <div className="max-w-7xl mx-auto px-6 flex flex-col md:flex-row items-center justify-between space-y-4 md:space-y-0">
        {/* Left: Branding */}
        <div className="text-center md:text-left">
          <h2 className="text-lg font-semibold text-indigo-700">SentinelIQ</h2>
          <p className="text-sm text-gray-500">AI-powered email intelligence</p>
        </div>

        {/* Center: Links */}
        <div className="flex space-x-6 text-sm">
          <a href="/privacy" className="hover:underline text-gray-700">
            Privacy Policy
          </a>
          <a href="/docs" className="hover:underline text-gray-700">
            API Docs
          </a>
          <a href="/contact" className="hover:underline text-gray-700">
            Contact
          </a>
        </div>

        {/* Right: Signature */}
        <div className="text-sm text-gray-500 text-center md:text-right">
          © {new Date().getFullYear()} SentinelIQ. Built by Jerome Uwaneme 👑
        </div>
      </div>
    </footer>
  );
}
