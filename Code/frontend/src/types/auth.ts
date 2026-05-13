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

export interface UserVO {
  userUuid: string
  username: string
  locked: boolean
}
