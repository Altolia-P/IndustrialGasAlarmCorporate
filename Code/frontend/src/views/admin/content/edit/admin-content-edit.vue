<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ContentType, ContentStatus } from '@/types/content'
import { contentApi } from '@/api/content'
import { categoryApi } from '@/api/category'
import { CategoryType } from '@/types/category'

const router = useRouter()
const route = useRoute()

const isEdit = ref(!!route.params.uuid)
const loading = ref(false)
const pageType = (route.query.type as string) || ContentType.SOLUTION

const form = reactive({
  title: '',
  type: pageType,
  categoryUuid: '',
  body: '',
  coverImage: null as File | null,
  status: ContentStatus.DRAFT
})

const categories = ref<{ categoryUuid: string; name: string }[]>([])

const submitting = ref(false)
const coverInput = ref<HTMLInputElement | null>(null)

async function fetchCategories() {
  try {
    categories.value = await categoryApi.getAdminCategories(CategoryType.CONTENT_CATEGORY)
  } catch {
    // categories remain empty
  }
}

async function fetchContent() {
  loading.value = true
  try {
    const detail = await contentApi.getAdminDetail(route.params.uuid as string)
    form.title = detail.title
    form.type = detail.type
    form.categoryUuid = detail.categoryUuid
    form.body = detail.body || ''
    form.status = detail.status
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '加载内容数据失败')
    router.push('/admin/contents')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchCategories()
  if (isEdit.value) {
    fetchContent()
  }
})

async function handleSubmit() {
  if (!form.title || !form.categoryUuid) {
    ElMessage.warning('请填写标题和分类')
    return
  }
  submitting.value = true
  try {
    const fd = new FormData()
    fd.append('title', form.title)
    fd.append('type', form.type)
    fd.append('categoryUuid', form.categoryUuid)
    fd.append('body', form.body)
    fd.append('status', form.status)
    if (form.coverImage) fd.append('coverImage', form.coverImage)
    if (isEdit.value) {
      await contentApi.update(route.params.uuid as string, fd)
    } else {
      await contentApi.create(fd)
    }
    ElMessage.success(isEdit.value ? '保存成功' : '创建成功')
    router.push('/admin/contents')
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '操作失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

const MAX_FILE_SIZE = 5 * 1024 * 1024 // 5MB

function onCoverChange(e: Event) {
  const input = e.target as HTMLInputElement
  if (input.files && input.files[0]) {
    if (input.files[0].size > MAX_FILE_SIZE) {
      ElMessage.warning('文件大小不能超过5MB')
      return
    }
    form.coverImage = input.files[0]
  }
}

function handleCancel() {
  router.push('/admin/contents')
}
</script>

<template>
  <div class="admin-content-edit">
    <div class="form-card" v-loading="loading">
      <h2 class="form-title">{{ isEdit ? '编辑内容' : '新增内容' }}</h2>

      <el-form v-if="!loading" :model="form" label-width="100px" class="edit-form">
        <el-form-item label="内容类型" required>
          <el-radio-group v-model="form.type">
            <el-radio value="SOLUTION">解决方案</el-radio>
            <el-radio value="NEWS">新闻</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="标题" required>
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>

        <el-form-item label="所属分类" required>
          <el-select v-model="form.categoryUuid" placeholder="请选择分类" style="width:100%">
            <el-option v-for="cat in categories" :key="cat.categoryUuid" :label="cat.name" :value="cat.categoryUuid" />
          </el-select>
        </el-form-item>

        <el-form-item label="内容">
          <el-input v-model="form.body" type="textarea" :rows="12" placeholder="请输入正文内容..." />
        </el-form-item>

        <el-form-item label="封面图片">
          <div class="upload-area">
            <input ref="coverInput" type="file" accept="image/jpeg,image/jpg,image/png,image/webp" style="display:none" @change="onCoverChange" />
            <el-button type="primary" plain @click="coverInput?.click()">选择图片</el-button>
            <span v-if="form.coverImage" class="upload-name">{{ (form.coverImage as File).name }}</span>
            <span v-else class="upload-tip">支持 jpg、png、webp，≤5MB</span>
          </div>
        </el-form-item>

        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio value="DRAFT">草稿</el-radio>
            <el-radio value="PUBLISHED">发布</el-radio>
          </el-radio-group>
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
.admin-content-edit {
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

.upload-name {
  font-size: 13px;
  color: #374151;
  max-width: 260px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.form-actions {
  display: flex;
  gap: 12px;
}
</style>
