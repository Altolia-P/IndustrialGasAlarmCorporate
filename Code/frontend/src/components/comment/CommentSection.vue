<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { CommentVO } from '@/types/comment'

const props = defineProps<{
  fetchComments: () => Promise<CommentVO[]>
  addComment: (content: string) => Promise<CommentVO>
}>()

const comments = ref<CommentVO[]>([])
const content = ref('')
const loading = ref(false)
const sending = ref(false)

const authorTagType = (type: string) => {
  switch (type) {
    case 'ADMIN': return 'danger'
    case 'STAFF': return 'primary'
    default: return 'success'
  }
}

const authorLabel = (type: string) => {
  switch (type) {
    case 'ADMIN': return '管理员'
    case 'STAFF': return '员工'
    default: return '用户'
  }
}

async function load() {
  loading.value = true
  try {
    comments.value = await props.fetchComments()
  } catch {
    comments.value = []
  } finally {
    loading.value = false
  }
}

async function send() {
  const text = content.value.trim()
  if (!text) {
    ElMessage.warning('请输入评论内容')
    return
  }
  sending.value = true
  try {
    const comment = await props.addComment(text)
    comments.value.push(comment)
    content.value = ''
    ElMessage.success('评论发送成功')
  } catch {
    ElMessage.error('发送失败')
  } finally {
    sending.value = false
  }
}

onMounted(() => {
  load()
})
</script>

<template>
  <div class="comment-section">
    <h4 class="comment-title">交流记录</h4>

    <div v-if="loading" class="comment-loading">
      <el-skeleton :rows="2" animated />
    </div>

    <div v-else-if="comments.length === 0" class="comment-empty">
      暂无交流记录
    </div>

    <div v-else class="comment-list">
      <div v-for="item in comments" :key="item.commentUuid" class="comment-item">
        <div class="comment-head">
          <el-tag size="small" :type="authorTagType(item.authorType)">
            {{ authorLabel(item.authorType) }}
          </el-tag>
          <span class="comment-author">{{ item.authorName }}</span>
          <span class="comment-time">{{ item.createdAt }}</span>
        </div>
        <div class="comment-body">{{ item.content }}</div>
      </div>
    </div>

    <div class="comment-input-area">
      <el-input
        v-model="content"
        type="textarea"
        :rows="3"
        placeholder="输入回复内容..."
        maxlength="500"
        show-word-limit
      />
      <div class="comment-send-row">
        <el-button type="primary" :loading="sending" @click="send" size="small">
          发送
        </el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.comment-section {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #f3f4f6;
}

.comment-title {
  font-size: 15px;
  font-weight: 600;
  color: #374151;
  margin: 0 0 16px;
}

.comment-loading {
  padding: 16px 0;
}

.comment-empty {
  text-align: center;
  padding: 24px;
  color: #9ca3af;
  font-size: 14px;
  background: #f9fafb;
  border-radius: 8px;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
}

.comment-item {
  background: #f9fafb;
  border-radius: 8px;
  padding: 12px 16px;
}

.comment-head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.comment-author {
  font-size: 13px;
  font-weight: 600;
  color: #374151;
}

.comment-time {
  font-size: 12px;
  color: #9ca3af;
  margin-left: auto;
}

.comment-body {
  font-size: 14px;
  color: #1f2937;
  line-height: 1.6;
}

.comment-input-area {
  margin-top: 8px;
}

.comment-send-row {
  margin-top: 8px;
  display: flex;
  justify-content: flex-end;
}
</style>
