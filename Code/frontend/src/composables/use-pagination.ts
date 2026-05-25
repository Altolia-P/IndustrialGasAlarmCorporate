import { ref, computed } from 'vue'

export interface PaginationState {
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export function usePagination(initialSize = 20) {
  const state = ref<PaginationState>({
    page: 1,
    size: initialSize,
    totalElements: 0,
    totalPages: 0
  })

  const backendPage = computed(() => state.value.page - 1)

  function setTotal(totalElements: number, totalPages: number) {
    state.value.totalElements = totalElements
    state.value.totalPages = totalPages
  }

  function goToPage(page: number) {
    state.value.page = page
  }

  function reset() {
    state.value.page = 1
    state.value.totalElements = 0
    state.value.totalPages = 0
  }

  return { state, backendPage, setTotal, goToPage, reset }
}
