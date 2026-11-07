import { redirect } from "next/navigation";
import { Suspense } from "react";

import { AuthSplit } from "@/components/auth-split";
import { etatInstallation } from "@/lib/auth/installation";
import { LoginForm } from "./login-form";

export default async function LoginPage() {
  // Première utilisation : l'assistant d'installation passe avant la connexion
  const installation = await etatInstallation();
  if (installation && !installation.installee) redirect("/installation");

  return (
    <AuthSplit>
      <Suspense>
        <LoginForm />
      </Suspense>
    </AuthSplit>
  );
}
