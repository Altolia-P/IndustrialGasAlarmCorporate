export interface Result<T> {
  code: number
  message: string
  data: T
  success: boolean
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}
