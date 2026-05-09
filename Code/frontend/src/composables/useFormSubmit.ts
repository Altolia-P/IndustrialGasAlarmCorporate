import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useLoading } from './useLoading'

export function useFormSubmit<D, R>(
  apiFn: (dto: D) => Promise<R>,
  options?: { successMsg?: string; onSuccess?: (data: R) => void }
) {
  const { loading, start, stop } = useLoading()
  const error = ref<string>('')

  async function submit(dto: D) {
    error.value = ''
    start()
    try {
      const data = await apiFn(dto)
      if (options?.successMsg) {
        ElMessage.success(options.successMsg)
      }
      options?.onSuccess?.(data)
      return data
    } catch (e: unknown) {
      const msg = (e as { message?: string }).message || '操作失败'
      error.value = msg
      ElMessage.error(msg)
      throw e
    } finally {
      stop()
    }
  }

  return { loading, error, submit }
}
