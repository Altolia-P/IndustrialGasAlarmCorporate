<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { downloadFileApi } from '@/api/download-file'
import { useLoading } from '@/composables/use-loading'
import { usePagination } from '@/composables/use-pagination'
import type { DownloadFileVO } from '@/types/download-file'

const { loading, start: startLoading, stop: stopLoading } = useLoading()
const { state: pagination, setTotal, goToPage } = usePagination(20)
const files = ref<DownloadFileVO[]>([])

const uploadVisible = ref(false)
const uploadLoading = ref(false)
const uploadFile = ref<File | null>(null)
const uploadDisplayName = ref('')

async function fetchFiles() {
  startLoading()
  try {
    const result = await downloadFileApi.getAdminList({
      page: pagination.value.page,
      size: pagination.value.size
    })
    files.value = result.content
    setTotal(result.totalElements, result.totalPages)
  } catch {
    ElMessage.error('加载文件列表失败')
  } finally {
    stopLoading()
  }
}

function handlePageChange(page: number) {
  goToPage(page)
  fetchFiles()
}

function openUpload() {
  uploadFile.value = null
  uploadDisplayName.value = ''
  uploadVisible.value = true
}

function onFileChange(file: File) {
  uploadFile.value = file
  if (!uploadDisplayName.value) {
    uploadDisplayName.value = file.name
  }
}

async function handleUpload() {
  if (!uploadFile.value) {
    ElMessage.warning('请选择文件')
    return
  }
  uploadLoading.value = true
  try {
    const formData = new FormData()
    formData.append('file', uploadFile.value)
    if (uploadDisplayName.value) {
      formData.append('displayName', uploadDisplayName.value)
    }
    await downloadFileApi.upload(formData)
    ElMessage.success('上传成功')
    uploadVisible.value = false
    fetchFiles()
  } catch {
    ElMessage.error('上传失败')
  } finally {
    uploadLoading.value = false
  }
}

async function handleDelete(file: DownloadFileVO) {
  try {
    await ElMessageBox.confirm(`确定删除「${file.displayName}」吗？此操作不可恢复。`, '删除确认', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  try {
    await downloadFileApi.remove(file.downloadUuid)
    ElMessage.success('删除成功')
    fetchFiles()
  } catch {
    ElMessage.error('删除失败')
  }
}

function formatSize(bytes: number): string {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

function formatType(contentType: string): string {
  return contentType.split('/').pop()?.toUpperCase() || contentType
}

onMounted(fetchFiles)
</script>

<template>
  <div class="download-page">
    <div class="page-header">
      <h3>下载中心管理</h3>
      <el-button type="primary" @click="openUpload">上传文件</el-button>
    </div>

    <el-card v-loading="loading">
      <el-table v-if="files.length > 0" :data="files" stripe>
        <el-table-column prop="displayName" label="显示名称" min-width="180" />
        <el-table-column prop="originalName" label="原始文件名" min-width="200" />
        <el-table-column label="大小" width="100">
          <template #default="{ row }">{{ formatSize(row.fileSize) }}</template>
        </el-table-column>
        <el-table-column label="类型" width="100">
          <template #default="{ row }">{{ formatType(row.contentType) }}</template>
        </el-table-column>
        <el-table-column prop="createdAt" label="上传时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" size="small" text @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && files.length === 0" description="暂无下载文件，点击「上传文件」添加" />

      <div v-if="pagination.totalPages > 1" class="pagination-wrap">
        <el-pagination
          :current-page="pagination.page"
          :page-size="pagination.size"
          :total="pagination.totalElements"
          layout="prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="uploadVisible" title="上传文件" width="480px" :close-on-click-modal="false">
      <el-form label-position="top">
        <el-form-item label="选择文件">
          <el-upload
            :auto-upload="false"
            :limit="1"
            :on-change="(f: any) => onFileChange(f.raw)"
            :on-exceed="() => ElMessage.warning('只能上传一个文件')"
            accept=".pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.zip,.rar,.txt,.csv,.jpg,.jpeg,.png"
            drag
          >
            <div class="upload-placeholder">
              <p>将文件拖到此处，或点击选择</p>
              <p class="upload-hint">支持 pdf/doc/xls/ppt/zip/txt/csv/jpg/png 格式</p>
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="显示名称（可选，默认使用文件名）">
          <el-input v-model="uploadDisplayName" placeholder="输入在下载页展示的名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadVisible = false">取消</el-button>
        <el-button type="primary" :loading="uploadLoading" @click="handleUpload">上传</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.download-page {
  padding: 0;
}
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.page-header h3 {
  margin: 0;
  font-size: 18px;
}
.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
.upload-placeholder {
  text-align: center;
  padding: 16px 0;
}
.upload-placeholder p {
  margin: 0;
  font-size: 14px;
  color: #606266;
}
.upload-hint {
  margin-top: 8px !important;
  font-size: 12px !important;
  color: #909399 !important;
}
</style>
