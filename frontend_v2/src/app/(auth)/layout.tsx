export default function AuthLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="ko">
      <body className="body">{children}</body>
    </html>
  );
}
