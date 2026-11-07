"use client";

import { Eye, EyeOff } from "lucide-react";
import { useTranslations } from "next-intl";
import { useState } from "react";
import { Controller, type Control, type FieldPath, type FieldValues } from "react-hook-form";

import { Field, FieldError, FieldLabel } from "@/components/ui/field";
import { Input } from "@/components/ui/input";

type Props<T extends FieldValues> = {
  control: Control<T>;
  name: FieldPath<T>;
  /** Libellé déjà traduit. */
  label: string;
  type?: React.HTMLInputTypeAttribute;
  autoComplete?: string;
  inputMode?: React.HTMLAttributes<HTMLInputElement>["inputMode"];
  autoFocus?: boolean;
};

/** Champ de formulaire (react-hook-form + shadcn Field) : libellé, saisie, message d'erreur traduit. */
export function TextField<T extends FieldValues>({ control, name, label, type = "text", autoComplete, inputMode, autoFocus }: Props<T>) {
  const t = useTranslations("common");
  const [visible, setVisible] = useState(false);
  const estMotDePasse = type === "password";
  const champ = (field: { name: string }, invalid: boolean, props: object) => (
    <Input
      {...props}
      id={field.name}
      type={estMotDePasse && visible ? "text" : type}
      autoComplete={autoComplete}
      inputMode={inputMode}
      autoFocus={autoFocus}
      aria-invalid={invalid}
      className={estMotDePasse ? "pr-9" : undefined}
    />
  );
  return (
    <Controller
      control={control}
      name={name}
      render={({ field, fieldState }) => (
        <Field data-invalid={fieldState.invalid}>
          <FieldLabel htmlFor={field.name}>{label}</FieldLabel>
          {estMotDePasse ? (
            <div className="relative">
              {champ(field, fieldState.invalid, field)}
              <button
                type="button"
                onClick={() => setVisible((v) => !v)}
                aria-label={visible ? t("masquerMotDePasse") : t("afficherMotDePasse")}
                aria-pressed={visible}
                className="absolute inset-y-0 right-0 flex w-9 items-center justify-center rounded-r-lg text-muted-foreground hover:text-foreground focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-ring/50"
              >
                {visible ? <EyeOff className="size-4" aria-hidden /> : <Eye className="size-4" aria-hidden />}
              </button>
            </div>
          ) : (
            champ(field, fieldState.invalid, field)
          )}
          {fieldState.error?.message ? <FieldError>{fieldState.error.message}</FieldError> : null}
        </Field>
      )}
    />
  );
}
