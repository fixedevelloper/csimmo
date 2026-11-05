import { cookies } from "next/headers";
import { getRequestConfig } from "next-intl/server";

import { DEFAULT_LOCALE, estLocale, LOCALE_COOKIE } from "./locales";

export default getRequestConfig(async () => {
  const valeur = (await cookies()).get(LOCALE_COOKIE)?.value;
  const locale = estLocale(valeur) ? valeur : DEFAULT_LOCALE;
  return {
    locale,
    messages: (await import(`../../messages/${locale}.json`)).default,
    timeZone: "Africa/Douala",
  };
});
