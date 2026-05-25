<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const isEdit = ref(!!route.params.uuid)

const form = reactive({
  title: '',
  type: 'SOLUTION',
  categoryUuid: '',
  body: '',
  coverImage: null as File | null,
  status: 'DRAFT'
})

const categories = ref([
  { categoryUuid: '1', name: '解决方案' },
  { categoryUuid: '2', name: '公司新闻' },
  { categoryUuid: '3', name: '产品发布' },
  { categoryUuid: '4', name: '行业动态' }
])

const submitting = ref(false)

function handleSubmit() {
  if (!form.title || !form.categoryUuid) {
    ElMessage.warning('请填写标题和分类')
    return
  }
  submitting.value = true
  setTimeout(() => {
    submitting.value = false
    ElMessage.success(isEdit.value ? '保存成功' : '创建成功')
    router.push('/admin/contents')
  }, 800)
}

function handleCancel() {
  router.push('/admin/contents')
}
</script>

<template>
  <div class="admin-content-edit">
    <div class="form-card">
      <h2 class="form-title">{{ isEdit ? '编辑内容' : '新增内容' }}</h2>

      <el-form :model="form" label-width="100px" class="edit-form">
        <el-form-item label="标题" required>
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>

        <el-form-item label="类型" required>
          <el-radio-group v-model="form.type">
            <el-radio value="SOLUTION">解决方案</el-radio>
            <el-radio value="NEWS">新闻</el-radio>
          </el-radio-group>
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
            <el-button type="primary" plain>选择图片</el-button>
            <span class="upload-tip">支持 jpg、png、webp，≤5MB</span>
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

.form-actions {
  display: flex;
  gap: 12px;
}
</style>
