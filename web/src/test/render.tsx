import { render } from "@testing-library/react";
import { NextIntlClientProvider } from "next-intl";
import type { ReactElement } from "react";

import en from "../../messages/en.json";
import fr from "../../messages/fr.json";

/** Rend un composant avec next-intl (fr par défaut). */
export function renderIntl(ui: ReactElement, locale: "fr" | "en" = "fr") {
  return render(
    <NextIntlClientProvider locale={locale} messages={locale === "fr" ? fr : en} timeZone="Africa/Douala">
      {ui}
    </NextIntlClientProvider>,
  );
}
