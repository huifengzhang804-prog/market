// export enum FormTypeText {
//     MOBILE = '电话',
//     CITIZEN_ID = '身份证',
//     TEXT = '文本',
//     NUMBER = '数字',
//     IMAGE = '图片',
//     DATE = '日期',
//     TIME = '时间',
//     DATETIME = '日期时间',
//     REMARK = '小票备注(同城/到店小票打印专用备注)',
// }

export enum FormType {
    MOBILE = 'MOBILE',
    CITIZEN_ID = 'CITIZEN_ID',
    TEXT = 'TEXT',
    NUMBER = 'NUMBER',
    IMAGE = 'IMAGE',
    DATE = 'DATE',
    TIME = 'TIME',
    DATETIME = 'DATETIME',
    REMARK = 'REMARK',
}

export const formTypeText: Record<FormType, string> = {
    [FormType.MOBILE]: '电话',
    [FormType.CITIZEN_ID]: '身份证',
    [FormType.TEXT]: '文本',
    [FormType.NUMBER]: '数字',
    [FormType.IMAGE]: '图片',
    [FormType.DATE]: '日期',
    [FormType.TIME]: '时间',
    [FormType.DATETIME]: '日期时间',
    [FormType.REMARK]: '小票备注(同城/到店小票打印专用备注)',
}
