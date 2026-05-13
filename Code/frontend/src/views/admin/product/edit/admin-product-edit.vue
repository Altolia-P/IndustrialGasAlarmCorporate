<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ProductStatus } from '@/types/product'
import { productApi } from '@/api/product'
import { adminProducts } from '@/data/content'

const router = useRouter()
const route = useRoute()

const isEdit = ref(!!route.params.uuid)

const form = reactive({
  name: '',
  categoryUuid: '',
  description: '',
  status: 'DRAFT',
  coverImage: null as File | null,
  images: [] as File[],
  attributes: [] as { attrKey: string; attrVal: string }[]
})

const categories = ref([
  { categoryUuid: '1', name: '气体检测仪' },
  { categoryUuid: '2', name: '控制系统' },
  { categoryUuid: '3', name: '传感器' },
  { categoryUuid: '4', name: '火灾报警' }
])

const submitting = ref(false)

function generateProductUuid(): string {
  const today = new Date()
  const dateStr =
    today.getFullYear().toString() +
    String(today.getMonth() + 1).padStart(2, '0') +
    String(today.getDate()).padStart(2, '0') +
    String(today.getHours()).padStart(2, '0') +
    String(today.getMinutes()).padStart(2, '0') +
    String(today.getSeconds()).padStart(2, '0')
  return `P-${dateStr}`
}

function formatNow(): string {
  const d = new Date()
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

function handleCoverChange(file: File) {
  form.coverImage = file
}

function handleImagesChange(files: File[]) {
  form.images = files
}

function addAttribute() {
  form.attributes.push({ attrKey: '', attrVal: '' })
}

function removeAttribute(index: number) {
  form.attributes.splice(index, 1)
}

async function handleSubmit() {
  if (!form.name || !form.categoryUuid) {
    ElMessage.warning('请填写产品名称和分类')
    return
  }
  submitting.value = true
  try {
    const fd = new FormData()
    fd.append('name', form.name)
    fd.append('categoryUuid', form.categoryUuid)
    fd.append('description', form.description)
    fd.append('status', form.status)
    if (form.coverImage) fd.append('coverImage', form.coverImage)
    form.images.forEach((img) => fd.append('images', img))
    form.attributes.forEach((attr, i) => {
      fd.append(`attributes[${i}].attrKey`, attr.attrKey)
      fd.append(`attributes[${i}].attrVal`, attr.attrVal)
    })
    if (isEdit.value) {
      await productApi.update(route.params.uuid as string, fd)
      const existing = adminProducts.find((p) => p.productUuid === route.params.uuid)
      if (existing) {
        existing.name = form.name
        existing.description = form.description
        existing.status = form.status as ProductStatus
        existing.categoryName = categories.value.find((c) => c.categoryUuid === form.categoryUuid)?.name ?? ''
      }
    } else {
      await productApi.create(fd)
      adminProducts.push({
        productUuid: generateProductUuid(),
        name: form.name,
        description: form.description,
        coverImage: '',
        categoryUuid: form.categoryUuid,
        categoryName: categories.value.find((c) => c.categoryUuid === form.categoryUuid)?.name ?? '',
        status: form.status as ProductStatus,
        createdAt: formatNow()
      })
    }
    ElMessage.success(isEdit.value ? '保存成功' : '创建成功')
    router.push('/admin/products')
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '操作失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

function handleCancel() {
  router.push('/admin/products')
}
</script>

<template>
  <div class="admin-product-edit">
    <div class="form-card">
      <h2 class="form-title">{{ isEdit ? '编辑产品' : '新增产品' }}</h2>

      <el-form :model="form" label-width="100px" class="edit-form">
        <el-form-item label="产品名称" required>
          <el-input v-model="form.name" placeholder="请输入产品名称" />
        </el-form-item>

        <el-form-item label="所属分类" required>
          <el-select v-model="form.categoryUuid" placeholder="请选择分类" style="width:100%">
            <el-option v-for="cat in categories" :key="cat.categoryUuid" :label="cat.name" :value="cat.categoryUuid" />
          </el-select>
        </el-form-item>

        <el-form-item label="产品描述">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入产品描述" />
        </el-form-item>

        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio value="DRAFT">草稿</el-radio>
            <el-radio value="PUBLISHED">上架</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="封面图片">
          <div class="upload-area">
            <el-button type="primary" plain>选择图片</el-button>
            <span class="upload-tip">支持 jpg、png、webp，≤5MB</span>
          </div>
        </el-form-item>

        <el-form-item label="产品图片">
          <div class="upload-area">
            <el-button type="primary" plain>选择图片</el-button>
            <span class="upload-tip">支持多张，单张≤5MB</span>
          </div>
        </el-form-item>

        <el-form-item label="产品属性">
          <div class="attributes-area">
            <div v-for="(attr, index) in form.attributes" :key="index" class="attribute-row">
              <el-input v-model="attr.attrKey" placeholder="属性名" style="width:180px" />
              <el-input v-model="attr.attrVal" placeholder="属性值" style="width:240px" />
              <el-button type="danger" plain size="small" @click="removeAttribute(index)">删除</el-button>
            </div>
            <el-button type="primary" plain size="small" @click="addAttribute">+ 添加属性</el-button>
          </div>
        </el-form-item>

        <el-form-item>
          <div class="form-actions">
            <el-button type="primary" :loading="submitting" @click="handleSubmit">
              {{ isEdit ? '保存' : '创建' }}
            </el-button>
            <el-button @click="handleCancel">取消</el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.admin-product-edit {
  max-width: 800px;
}

.form-card {
  background: #ffffff;
  border-radius: 8px;
  padding: 32px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.form-title {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 32px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f3f4f6;
}

.edit-form {
  max-width: 640px;
}

.upload-area {
  display: flex;
  align-items: center;
  gap: 12px;
}

.upload-tip {
  font-size: 12px;
  color: #9ca3af;
}

.attributes-area {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.attribute-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.form-actions {
  display: flex;
  gap: 12px;
}

@media (max-width: 640px) {
  .form-card {
    padding: 20px;
  }
  .attribute-row {
    flex-wrap: wrap;
  }
}
</style>
