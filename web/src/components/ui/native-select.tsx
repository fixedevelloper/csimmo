import { ChevronDownIcon } from "lucide-react"
import * as React from "react"
import { cn } from "cn"

/**
 * Liste déroulante native (accessible, tactile) au même style qu'un Input : 40 px, fond grisé, coins `rounded-lg`, chevron.
 * `className` s'applique à l'enveloppe (largeur, marges) ; le reste va au `<select>`.
 */
function NativeSelect({ className, children, ...props }: React.ComponentProps<"select">) {
  return (
    <div data-slot="native-select-wrapper" className={cn("relative inline-flex", className)}>
      <select
        data-slot="native-select"
        className="h-10 w-full min-w-0 appearance-none rounded-lg border border-input bg-muted/40 py-1 pr-9 pl-3 text-base transition-colors outline-none hover:border-foreground/25 focus-visible:border-primary focus-visible:bg-background focus-visible:ring-3 focus-visible:ring-ring/50 disabled:cursor-not-allowed disabled:opacity-50 aria-invalid:border-destructive aria-invalid:ring-3 aria-invalid:ring-destructive/20 md:text-sm dark:bg-input/30"
        {...props}
      >
        {children}
      </select>
      <ChevronDownIcon aria-hidden className="pointer-events-none absolute top-1/2 right-3 size-4 -translate-y-1/2 text-muted-foreground" />
    </div>
  )
}

export { NativeSelect }
