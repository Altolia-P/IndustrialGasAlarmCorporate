export enum CategoryType {
  PRODUCT_CATEGORY = 'PRODUCT_CATEGORY',
  CONTENT_CATEGORY = 'CONTENT_CATEGORY'
}

export const CategoryTypeMap: Record<CategoryType, string> = {
  [CategoryType.PRODUCT_CATEGORY]: '产品分类',
  [CategoryType.CONTENT_CATEGORY]: '内容分类'
}

export interface CategoryVO {
  categoryUuid: string
  name: string
  type: CategoryType
  parentUuid: string | null
  sortOrder: number
  children?: CategoryVO[]
}
