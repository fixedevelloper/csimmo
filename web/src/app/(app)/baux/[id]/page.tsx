import { BailFiche } from "@/components/baux/bail-fiche";

export default async function BailPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = await params;
  return <BailFiche id={id} />;
}
