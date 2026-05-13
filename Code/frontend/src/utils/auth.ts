const TOKEN_KEY = 'token'
const ROLE_KEY = 'role'

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token)
}

export function removeToken(): void {
  localStorage.removeItem(TOKEN_KEY)
}

export function hasToken(): boolean {
  return !!localStorage.getItem(TOKEN_KEY)
}

export function getRole(): string {
  return localStorage.getItem(ROLE_KEY) || ''
}

export function setRole(role: string): void {
  localStorage.setItem(ROLE_KEY, role)
}

export function removeRole(): void {
  localStorage.removeItem(ROLE_KEY)
}
