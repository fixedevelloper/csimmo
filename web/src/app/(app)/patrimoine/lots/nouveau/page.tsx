import { Suspense } from "react";

import { LotCreation } from "./lot-creation";

export default function NouveauLotPage() {
  return (
    <Suspense>
      <LotCreation />
    </Suspense>
  );
}
