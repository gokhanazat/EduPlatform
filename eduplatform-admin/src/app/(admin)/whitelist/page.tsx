"use client"
import { useEffect, useState } from "react"
import { supabase } from "@/lib/supabase/client"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"

type WhitelistEntry = { id: string; email: string; sicil_no: string; city: string; is_active: boolean; added_at: string; notes: string }

export default function WhitelistPage() {
  const [entries, setEntries] = useState<WhitelistEntry[]>([])
  const [search, setSearch] = useState("")
  const [showAdd, setShowAdd] = useState(false)
  const [newEmail, setNewEmail] = useState("")
  const [newSicil, setNewSicil] = useState("")
  const [newCity, setNewCity] = useState("")
  const [newNotes, setNewNotes] = useState("")
  const [loading, setLoading] = useState(false)

  async function load() {
    let q = supabase.from("whitelist").select("*").order("added_at", { ascending: false })
    if (search) q = q.ilike("email", `%${search}%`)
    const { data } = await q
    setEntries(data ?? [])
  }

  useEffect(() => { load() }, [search])

  async function addEntry() {
    if (!newEmail && !newSicil) {
      alert("E-posta veya Sicil No gereklidir.")
      return
    }
    setLoading(true)
    const { error } = await supabase.from("whitelist").insert({ 
      email: newEmail || null, 
      sicil_no: newSicil || null,
      city: newCity, 
      notes: newNotes 
    })
    
    if (error) {
      console.error("Whitelist ekleme hatası:", error)
      alert("Hata: " + error.message)
      setLoading(false)
      return
    }

    setNewEmail(""); setNewSicil(""); setNewCity(""); setNewNotes(""); setShowAdd(false); setLoading(false)
    load()
  }

  async function toggleActive(id: string, current: boolean) {
    await supabase.from("whitelist").update({ is_active: !current }).eq("id", id)
    load()
  }

  async function deleteEntry(id: string) {
    if (!confirm("Bu kaydı silmek istediğinize emin misiniz?")) return
    await supabase.from("whitelist").delete().eq("id", id)
    load()
  }

  async function importCsv(file: File) {
    const text = await file.text()
    const rows = text.split("\n").slice(1) // skip header
    const inserts = rows.filter(r => r.trim()).map(row => {
      const [email, sicil_no, city, notes] = row.split(",").map(s => s?.trim()?.replace(/"/g, ""))
      return { email, sicil_no, city, notes }
    }).filter(r => r.email || r.sicil_no)
    const { error } = await supabase.from("whitelist").upsert(inserts, { onConflict: "email" })
    if (error) alert("CSV Yükleme Hatası: " + error.message)
    load()
  }

  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-semibold">Whitelist ({entries.length})</h1>
        <div className="flex gap-2">
          <label className="cursor-pointer">
            <Button variant="outline" asChild>
              <span>CSV Yükle</span>
            </Button>
            <input type="file" accept=".csv" className="hidden"
              onChange={e => e.target.files?.[0] && importCsv(e.target.files[0])} />
          </label>
          <Button onClick={() => setShowAdd(true)}>+ Ekle</Button>
        </div>
      </div>

      <Input placeholder="E-posta veya Sicil No ara..." value={search} onChange={e => setSearch(e.target.value)}
        className="max-w-xs" />

      <div className="text-xs text-slate-500">CSV formatı: email,sicil_no,city,notes (başlık satırı dahil)</div>

      <div className="border rounded-lg overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-slate-100">
            <tr>
              {["E-posta", "Sicil No", "Şehir", "Durum", "Eklenme", "Not", "İşlem"].map(h => (
                <th key={h} className="text-left px-4 py-3 font-medium text-slate-700">{h}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {entries.map(e => (
              <tr key={e.id} className="border-t hover:bg-slate-50">
                <td className="px-4 py-3">{e.email || "-"}</td>
                <td className="px-4 py-3">{e.sicil_no || "-"}</td>
                <td className="px-4 py-3">{e.city}</td>
                <td className="px-4 py-3">
                  <Badge variant={e.is_active ? "default" : "secondary"}>
                    {e.is_active ? "Aktif" : "Pasif"}
                  </Badge>
                </td>
                <td className="px-4 py-3 text-slate-500">{new Date(e.added_at).toLocaleDateString("tr-TR")}</td>
                <td className="px-4 py-3 text-slate-500">{e.notes}</td>
                <td className="px-4 py-3">
                  <div className="flex gap-1">
                    <Button size="sm" variant="ghost"
                      onClick={() => toggleActive(e.id, e.is_active)}>
                      {e.is_active ? "Pasif Yap" : "Aktif Yap"}
                    </Button>
                    <Button size="sm" variant="ghost" className="text-red-500"
                      onClick={() => deleteEntry(e.id)}>Sil</Button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Dialog open={showAdd} onOpenChange={setShowAdd}>
        <DialogContent>
          <DialogHeader><DialogTitle>Yeni Üye Ekle</DialogTitle></DialogHeader>
          <div className="space-y-3">
            <Input placeholder="E-posta adresi" value={newEmail} onChange={e => setNewEmail(e.target.value)} />
            <Input placeholder="Sicil No" value={newSicil} onChange={e => setNewSicil(e.target.value)} />
            <Input placeholder="Şehir *" value={newCity} onChange={e => setNewCity(e.target.value)} />
            <Input placeholder="Not (opsiyonel)" value={newNotes} onChange={e => setNewNotes(e.target.value)} />
            <Button className="w-full" onClick={addEntry} disabled={loading || (!newEmail && !newSicil) || !newCity}>
              {loading ? "Ekleniyor..." : "Ekle"}
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  )
}
