import type { Metadata } from "next";

import "./globals.css";

export const metadata: Metadata = {
  title: "CSImmo — Portail éditeur",
  description: "Gestion des clients, licences et versions de CSImmo",
  robots: { index: false, follow: false },
};

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="fr">
      <body className="antialiased">{children}</body>
    </html>
  );
}
