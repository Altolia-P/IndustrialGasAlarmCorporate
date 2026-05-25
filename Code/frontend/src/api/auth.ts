import request from '@/utils/request'
import type { LoginDTO, LoginWithCaptchaDTO, RegisterDTO, ResetPasswordDTO, LoginResultVO, CaptchaVO, UserVO } from '@/types/auth'

export const authApi = {
  login(dto: LoginDTO): Promise<LoginResultVO> {
    return request.post('/admin/login', dto)
  },
  loginWithCaptcha(dto: LoginWithCaptchaDTO): Promise<LoginResultVO> {
    return request.post('/admin/login', dto)
  },
  register(dto: RegisterDTO): Promise<null> {
    return request.post('/public/register', dto)
  },
  getCaptcha(): Promise<CaptchaVO> {
    return request.get('/admin/captcha')
  },
  getCurrentUser(): Promise<UserVO> {
    return request.get('/admin/currentUser')
  },
  logout(): Promise<null> {
    return request.post('/admin/logout')
  },
  resetPassword(dto: ResetPasswordDTO): Promise<null> {
    return request.post('/admin/resetPassword', dto)
  }
}
