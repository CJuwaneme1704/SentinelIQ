// // middleware.ts
// import { NextRequest, NextResponse } from 'next/server';

// export function middleware(req: NextRequest) {
//   const token = req.cookies.get('access_token');


//   if (!token) {
//     // Redirect to login if no JWT is found
//     return NextResponse.redirect(new URL('/login', req.url));
//   }

//   return NextResponse.next();
// }

// // Apply the middleware to protected routes
// export const config = {
//   matcher: ['/user_pages/protected/:path*'],
// };
