import request from '@/utils/request'
import type { LoginDTO, LoginResultVO, UserVO } from '@/types/auth'

export const authApi = {
  login(dto: LoginDTO): Promise<LoginResultVO> {
    return request.post('/admin/login', dto)
  },
  getCurrentUser(): Promise<UserVO> {
    return request.get('/admin/currentUser')
  }
}
