import { ref } from 'vue'

export function useLoading(initial = false) {
  const loading = ref(initial)
  function start() { loading.value = true }
  function stop() { loading.value = false }
  return { loading, start, stop }
}
