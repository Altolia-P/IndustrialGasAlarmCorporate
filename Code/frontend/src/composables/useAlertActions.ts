import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { alertApi } from '@/api/device'
import { AlertStatus } from '@/types/device'

export function useAlertActions(onSuccess: () => void) {
  const acting = ref(false)

  async function confirm(uuid: string) {
    try {
      await ElMessageBox.confirm('确认将该报警标记为"已确认"？', '确认报警', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'info'
      })
    } catch {
      return // user cancelled the dialog
    }
    acting.value = true
    try {
      await alertApi.confirm(uuid)
      ElMessage.success('报警已确认')
      onSuccess()
    } catch (e: unknown) {
      const err = e as { message?: string }
      ElMessage.error(err.message || '确认失败')
    } finally {
      acting.value = false
    }
  }

  async function resolve(uuid: string) {
    try {
      await ElMessageBox.confirm('确认将该报警标记为"已解决"？', '解决报警', {
        confirmButtonText: '确认解决',
        cancelButtonText: '取消',
        type: 'success'
      })
    } catch {
      return // user cancelled the dialog
    }
    acting.value = true
    try {
      await alertApi.resolve(uuid)
      ElMessage.success('报警已解决')
      onSuccess()
    } catch (e: unknown) {
      const err = e as { message?: string }
      ElMessage.error(err.message || '解决失败')
    } finally {
      acting.value = false
    }
  }

  async function closeAlert(uuid: string) {
    try {
      await ElMessageBox.confirm('确认关闭该报警？关闭后不可恢复。', '关闭报警', {
        confirmButtonText: '确认关闭',
        cancelButtonText: '取消',
        type: 'warning'
      })
    } catch {
      return // user cancelled the dialog
    }
    acting.value = true
    try {
      await alertApi.close(uuid)
      ElMessage.success('报警已关闭')
      onSuccess()
    } catch (e: unknown) {
      const err = e as { message?: string }
      ElMessage.error(err.message || '关闭失败')
    } finally {
      acting.value = false
    }
  }

  function statusTagType(status: string): 'danger' | 'warning' | 'info' | 'success' | '' {
    switch (status) {
      case AlertStatus.PENDING: return 'danger'
      case AlertStatus.CONFIRMED: return 'warning'
      case AlertStatus.RESOLVED: return 'success'
      case AlertStatus.CLOSED: return 'info'
      default: return ''
    }
  }

  return { acting, confirm, resolve, closeAlert, statusTagType }
}
