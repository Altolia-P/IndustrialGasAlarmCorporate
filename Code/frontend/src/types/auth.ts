export interface LoginDTO {
  username: string
  password: string
}

export interface LoginResultVO {
  token: string
  userUuid: string
  username: string
  role: string
}

export interface UserVO {
  userUuid: string
  username: string
  locked: boolean
}
