import * as XLSX from 'xlsx'

export interface ExportColumn {
  header: string
  key: string
}

export function useExport() {
  async function exportToExcel(
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    fetchFn: (...args: any) => Promise<{ content: any[] }>,
    params: any,
    columns: ExportColumn[],
    filename: string
  ) {
    const page = await fetchFn({ ...params, page: 1, size: 10000 })
    const rows = (page.content || []).map((row: any) => {
      const r: Record<string, string> = {}
      columns.forEach((col) => {
        r[col.header] = String(row[col.key] ?? '')
      })
      return r
    })

    const ws = XLSX.utils.json_to_sheet(rows, { header: columns.map((c) => c.header) })
    const wb = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(wb, ws, 'Sheet1')
    XLSX.writeFile(wb, `${filename}.xlsx`)
  }

  return { exportToExcel }
}
