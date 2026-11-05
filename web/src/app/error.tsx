"use client";

import { ErrorDisplay } from "@/components/error-display";

export default function Erreur({ error, reset }: { error: Error & { digest?: string }; reset: () => void }) {
  return <ErrorDisplay erreur={error} onRetry={reset} />;
}
