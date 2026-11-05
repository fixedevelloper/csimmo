"use client";

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
  return (
    <Controller
      control={control}
      name={name}
      render={({ field, fieldState }) => (
        <Field data-invalid={fieldState.invalid}>
          <FieldLabel htmlFor={field.name}>{label}</FieldLabel>
          <Input
            {...field}
            id={field.name}
            type={type}
            autoComplete={autoComplete}
            inputMode={inputMode}
            autoFocus={autoFocus}
            aria-invalid={fieldState.invalid}
          />
          {fieldState.error?.message ? <FieldError>{fieldState.error.message}</FieldError> : null}
        </Field>
      )}
    />
  );
}
