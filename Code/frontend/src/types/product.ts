export enum ProductStatus {
  DRAFT = 'DRAFT',
  PUBLISHED = 'PUBLISHED',
  UNPUBLISHED = 'UNPUBLISHED'
}

export const ProductStatusMap: Record<ProductStatus, string> = {
  [ProductStatus.DRAFT]: '草稿',
  [ProductStatus.PUBLISHED]: '已上架',
  [ProductStatus.UNPUBLISHED]: '已下架'
}

export interface ImageVO {
  url: string
  altText: string
  sortOrder: number
}

export interface AttributeVO {
  attrKey: string
  attrVal: string
}

export interface ProductVO {
  productUuid: string
  name: string
  description: string
  coverImage: string
  categoryUuid: string
  categoryName: string
  status: ProductStatus
  createdAt: string
}

export interface ProductDetailVO {
  productUuid: string
  name: string
  description: string
  coverImage: string
  images: ImageVO[]
  attributes: AttributeVO[]
  body?: string
  categoryUuid: string
  categoryName: string
  status: ProductStatus
  createdAt: string
}

export interface CreateProductDTO {
  name: string
  categoryUuid: string
  coverImage: File
  images: File[]
  description: string
  attributes: { attrKey: string; attrVal: string }[]
  status: ProductStatus
}

export interface UpdateProductDTO extends Partial<CreateProductDTO> {
  productUuid: string
}
