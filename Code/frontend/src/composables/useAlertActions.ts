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
      acting.value = true
      await alertApi.confirm(uuid)
      ElMessage.success('报警已确认')
      onSuccess()
    } catch {
      // cancelled
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
      acting.value = true
      await alertApi.resolve(uuid)
      ElMessage.success('报警已解决')
      onSuccess()
    } catch {
      // cancelled
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
      acting.value = true
      await alertApi.close(uuid)
      ElMessage.success('报警已关闭')
      onSuccess()
    } catch {
      // cancelled
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
