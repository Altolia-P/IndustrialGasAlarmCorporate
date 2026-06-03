export enum ContentType {
  SOLUTION = 'SOLUTION',
  NEWS = 'NEWS'
}

export const ContentTypeMap: Record<ContentType, string> = {
  [ContentType.SOLUTION]: '解决方案',
  [ContentType.NEWS]: '新闻动态'
}

export enum ContentStatus {
  DRAFT = 'DRAFT',
  PUBLISHED = 'PUBLISHED'
}

export const ContentStatusMap: Record<ContentStatus, string> = {
  [ContentStatus.DRAFT]: '草稿',
  [ContentStatus.PUBLISHED]: '已发布'
}

export interface ContentVO {
  contentUuid: string
  title: string
  summary: string
  coverImage: string
  type: ContentType
  categoryUuid: string
  categoryName: string
  status: ContentStatus
  createdAt: string
  updatedAt?: string
}

export interface ContentDetailVO {
  contentUuid: string
  title: string
  body: string
  coverImage: string
  type: ContentType
  categoryUuid: string
  categoryName: string
  status: ContentStatus
  createdAt: string
  updatedAt: string
}

export interface CreateContentDTO {
  title: string
  type: ContentType
  categoryUuid: string
  body: string
  coverImage?: File
  status: ContentStatus
}

export interface UpdateContentDTO extends Partial<CreateContentDTO> {
  contentUuid: string
}
