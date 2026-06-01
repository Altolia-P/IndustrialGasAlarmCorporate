<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ContentType, ContentStatus, ContentStatusMap } from '@/types/content'
import { contentApi } from '@/api/content'
import { categoryApi } from '@/api/category'
import { CategoryType } from '@/types/category'

const router = useRouter()
const route = useRoute()

const isEdit = ref(!!route.params.uuid)
const loading = ref(false)

const form = reactive({
  title: '',
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

async function fetchNews() {
  loading.value = true
  try {
    const detail = await contentApi.getAdminDetail(route.params.uuid as string)
    form.title = detail.title
    form.categoryUuid = detail.categoryUuid
    form.body = detail.body || ''
    form.status = detail.status
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '加载新闻数据失败')
    router.push('/admin/news')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchCategories()
  if (isEdit.value) {
    fetchNews()
  }
})

async function handleSubmit() {
  if (!form.title) {
    ElMessage.warning('请填写新闻标题')
    return
  }
  if (!form.categoryUuid) {
    ElMessage.warning('请选择新闻分类')
    return
  }
  if (!form.body) {
    ElMessage.warning('请填写新闻正文')
    return
  }
  submitting.value = true
  try {
    const fd = new FormData()
    fd.append('title', form.title)
    fd.append('type', ContentType.NEWS)
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
    router.push('/admin/news')
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '操作失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

function onCoverChange(e: Event) {
  const input = e.target as HTMLInputElement
  if (input.files && input.files[0]) {
    form.coverImage = input.files[0]
  }
}

function handleCancel() {
  router.push('/admin/news')
}
</script>

<template>
  <div class="admin-news-edit">
    <div class="form-card" v-loading="loading">
      <h2 class="form-title">{{ isEdit ? '编辑新闻' : '新增新闻' }}</h2>

      <el-form v-if="!loading" :model="form" label-width="100px" class="edit-form">
        <el-form-item label="新闻标题" required>
          <el-input v-model="form.title" placeholder="请输入新闻标题" maxlength="100" show-word-limit />
        </el-form-item>

        <el-form-item label="新闻分类" required>
          <el-select v-model="form.categoryUuid" placeholder="请选择分类" style="width:100%">
            <el-option v-for="cat in categories" :key="cat.categoryUuid" :label="cat.name" :value="cat.categoryUuid" />
          </el-select>
        </el-form-item>

        <el-form-item label="新闻正文" required>
          <el-input
            v-model="form.body"
            type="textarea"
            :rows="16"
            placeholder="请输入新闻正文内容..."
          />
        </el-form-item>

        <el-form-item label="封面图片">
          <div class="upload-area">
            <input ref="coverInput" type="file" accept="image/jpeg,image/png,image/webp" style="display:none" @change="onCoverChange" />
            <el-button type="primary" plain @click="coverInput?.click()">选择图片</el-button>
            <span v-if="form.coverImage" class="upload-name">{{ (form.coverImage as File).name }}</span>
            <span v-else class="upload-tip">支持 jpg、png、webp，≤5MB</span>
          </div>
        </el-form-item>

        <el-form-item label="发布状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="ContentStatus.DRAFT">草稿</el-radio>
            <el-radio :value="ContentStatus.PUBLISHED">发布</el-radio>
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
.admin-news-edit {
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
