export class FileHelper {
    /**
     * 根据文件路径获取文件名
     * @param filePath
     */
    static getFileName(filePath:string):string{
        return FileHelper.getFileFullName(filePath).name;
    }

    /**
     * 根据文件路径获取文件后缀
     */
    static getFileExtension(filePath:string):string{
        return FileHelper.getFileFullName(filePath).ext;
    }
    /**
     * 根据文件路径获取文件名与拓展名
     */
    static getFileFullName(filePath:string):FileFullName{
        if (!filePath){
            return {name:'',ext:''};
        }
        const fullName = filePath.split("/").pop();
        if (fullName == null){
            return {name:'', ext:''};
        }
        const lastIndexOfPoint = fullName.lastIndexOf(".");
        const fileName = fullName.substring(0, lastIndexOfPoint);
        const extension = fullName.substring(lastIndexOfPoint+1,);
        return {
            name:fileName,
            ext:extension
        }
    }


}
declare interface FileFullName{
    name:string,
    ext:string
}