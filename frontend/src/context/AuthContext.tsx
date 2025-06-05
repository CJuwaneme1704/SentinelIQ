'use client'; // 🔁 Tells Next.js this runs on the client side (needed for useState/useEffect)

import { createContext, useContext, useEffect, useState, ReactNode } from 'react';


const DEV_MODE = process.env.NEXT_PUBLIC_DEV_MODE === "true";



// 🧠 Define the shape of what our context will provide (like a contract)
type AuthContextType = {
  isAuthenticated: boolean;                      // 🟢 Are you logged in?
  setIsAuthenticated: (value: boolean) => void;  // 🔁 A function to update login status
  authChecked: boolean;                          // ✅ Has the app *finished checking* if you're logged in?
};

// 🏗️ Create the context — like building a mailbox where shared state will live
const AuthContext = createContext<AuthContextType>({
  isAuthenticated: false,              // 🧃 Default: assume not logged in
  setIsAuthenticated: () => {},        // ❌ Placeholder function
  authChecked: false,                  // ⏳ Haven’t checked login status yet
});

// 🧵 This is the "provider" — it wraps your app and gives all child components access to the context
export function AuthProvider({ children }: { children: ReactNode }) {
  // 🧩 Local state: start with assuming user is not logged in
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  // ⏲️ Track whether we’ve finished checking auth status
  const [authChecked, setAuthChecked] = useState(false);

  // 🧭 When the app starts, we check with the backend if the user is logged in
  useEffect(() => {
    if(DEV_MODE){
      setIsAuthenticated(true);
      setAuthChecked(true);
    } else{
      const checkAuth = async () => {
        try {
          const res = await fetch("http://localhost:8080/api/auth/check", {
            credentials: "include", // 🍪 Include cookies (JWT in HTTP-only cookie)
          });

          setIsAuthenticated(res.ok); // ✅ If backend returns 200 OK, we are logged in
        } catch (err) {
          console.error("Auth check failed:", err); // ❌ Handle connection errors
          setIsAuthenticated(false); // 🚫 Assume not logged in if there's an error
        } finally {
          setAuthChecked(true); // 🏁 We’re done checking (even if failed)
        }
      };

      checkAuth(); // 📞 Start the check on component mount
    }
  }, []);

  // 📦 Make auth state + update function available to all children components
  return (
    <AuthContext.Provider value={{ isAuthenticated, setIsAuthenticated, authChecked }}>
      {children}
    </AuthContext.Provider>
  );
}

// 🪄 Custom hook to let other components easily use the context
export const useAuth = () => useContext(AuthContext);
