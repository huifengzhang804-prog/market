export interface DownloadListRes {
    current: number
    pages: number
    records: DownloadList[]
    size: number
    total: number
    [property: string]: any
}

export interface DownloadList {
    createTime?: string
    dataCount?: number
    dataType?: string
    exportDataTypeStr?: string
    exportUserId?: string
    filePath?: string
    fileSize?: number
    id?: string
    shopId?: string
    status?: string
    userPhone?: string
    [property: string]: any
}
