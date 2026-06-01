export interface LoginDTO {
  username: string
  password: string
}

export interface LoginWithCaptchaDTO {
  username: string
  password: string
  captcha: string
  captchaToken: string
}

export interface RegisterDTO {
  username: string
  password: string
  phone: string
  company?: string
}

export interface LoginResultVO {
  token: string
  userUuid: string
  username: string
  role: string
}

export interface CaptchaVO {
  image: string
  token: string
}

export interface ResetPasswordDTO {
  username: string
  newPassword: string
}

export interface UserVO {
  userUuid: string
  username: string
  phone?: string
  company?: string
  lastLoginAt?: string
  locked: boolean
}

export interface UpdateProfileDTO {
  phone: string
  company?: string
}
