<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { CategoryType, CategoryTypeMap } from '@/types/category'
import { categoryApi } from '@/api/category'
import type { CategoryVO } from '@/types/category'

const tabType = ref<CategoryType>(CategoryType.PRODUCT_CATEGORY)
const categories = ref<CategoryVO[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = ref({ name: '', parentUuid: '', sortOrder: 0 })
const editingUuid = ref<string | null>(null)

const flatList = computed(() => {
  const result: (CategoryVO & { level: number })[] = []
  function flatten(list: CategoryVO[], level: number) {
    for (const item of list) {
      result.push({ ...item, level })
      if (item.children && item.children.length > 0) {
        flatten(item.children, level + 1)
      }
    }
  }
  flatten(categories.value, 0)
  return result
})

function parentOptions(currentUuid?: string) {
  return flatList.value
    .filter(c => c.type === tabType.value && c.categoryUuid !== currentUuid)
    .map(c => ({ label: '—'.repeat(c.level) + ' ' + c.name, value: c.categoryUuid }))
}

async function fetchCategories() {
  loading.value = true
  try {
    categories.value = await categoryApi.getAdminCategories(tabType.value)
  } catch {
    categories.value = []
    ElMessage.error('加载分类失败')
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingUuid.value = null
  dialogTitle.value = '新增分类'
  form.value = { name: '', parentUuid: '', sortOrder: 0 }
  dialogVisible.value = true
}

function openEdit(row: CategoryVO) {
  editingUuid.value = row.categoryUuid
  dialogTitle.value = '编辑分类'
  form.value = {
    name: row.name,
    parentUuid: row.parentUuid || '',
    sortOrder: row.sortOrder
  }
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!form.value.name.trim()) {
    ElMessage.warning('请输入分类名称')
    return
  }
  try {
    const parentUuid = form.value.parentUuid || ''
    if (editingUuid.value) {
      await categoryApi.update(editingUuid.value, {
        name: form.value.name,
        parentUuid,
        sortOrder: form.value.sortOrder
      })
      ElMessage.success('修改成功')
    } else {
      await categoryApi.create({
        name: form.value.name,
        type: tabType.value,
        parentUuid,
        sortOrder: form.value.sortOrder
      })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    await fetchCategories()
  } catch {
    ElMessage.error('操作失败')
  }
}

async function handleDelete(row: CategoryVO) {
  try {
    await ElMessageBox.confirm(`确定要删除分类「${row.name}」吗？`, '确认删除', { type: 'warning' })
    await categoryApi.remove(row.categoryUuid)
    ElMessage.success('删除成功')
    await fetchCategories()
  } catch {
    // cancelled
  }
}

function onTabChange() {
  fetchCategories()
}

onMounted(() => {
  fetchCategories()
})
</script>

<template>
  <div class="admin-category-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>分类管理</span>
          <el-button type="primary" @click="openCreate">新增分类</el-button>
        </div>
      </template>

      <el-tabs v-model="tabType" @tab-change="onTabChange">
        <el-tab-pane
          v-for="(label, key) in CategoryTypeMap"
          :key="key"
          :label="label"
          :name="key"
        />
      </el-tabs>

      <el-table :data="flatList" v-loading="loading" row-key="categoryUuid" default-expand-all>
        <el-table-column label="分类名称" min-width="200">
          <template #default="{ row }">
            <span :style="{ paddingLeft: row.level * 24 + 'px' }">{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="120">
          <template #default="{ row }">
            {{ CategoryTypeMap[row.type as CategoryType] }}
          </template>
        </el-table-column>
        <el-table-column label="排序" width="80" prop="sortOrder" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="480px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="分类名称">
          <el-input v-model="form.name" maxlength="50" />
        </el-form-item>
        <el-form-item label="上级分类">
          <el-select v-model="form.parentUuid" placeholder="无（顶级分类）" clearable style="width: 100%">
            <el-option
              v-for="opt in parentOptions(editingUuid ?? undefined)"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
