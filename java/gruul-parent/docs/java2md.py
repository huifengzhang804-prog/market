import logging
import re
from pathlib import Path
from typing import Optional

# 配置日志
logging.basicConfig(level=logging.INFO,
                    format='%(asctime)s - %(levelname)s - %(message)s')

class JavaToMdConverter:
    def __init__(self, output_dir: str):
        self.output_dir = Path(output_dir)
        self.code_block_count = 0

    def process_directory(self, java_root: str):
        output_folder = java_root
        """递归处理Java源代码目录"""
        java_path = Path(java_root + "/src/main/java")
        md_content = ''
        for file_path in java_path.rglob("*.java"):
            try:
                if file_path.is_file():
                    md_content += self.convert_file(file_path)
            except Exception as e:
                logging.error(f"处理文件失败 {file_path}: {str(e)}")
        if md_content:
            self.save_markdown(md_content, output_folder)
    def convert_file(self, java_path: Path) -> str:
        """转换单个Java文件"""
        # 读取源码
        try:
            with open(java_path, 'r', encoding='utf-8', errors='replace') as f:
                code_content = f.read()
        except UnicodeDecodeError:
            logging.warning(f"编码问题，跳过文件: {java_path}")
            return ''
        # 提取元数据
        class_name = java_path.stem
        package = self.extract_package(code_content)
        javadoc = self.extract_javadoc(code_content)

        # 构建Markdown内容
        md_content = self.build_markdown(
            java_path, package, class_name, code_content, javadoc)
        return md_content



    def build_markdown(self, java_path: Path, package: str, 
                      class_name: str, code: str, javadoc: str) -> str:
        """构建Markdown文档结构"""
        header = f"# {package}.{class_name}\n\n" if package else f"# {class_name}\n\n"
        code_block = f"```java\n{code}\n```\n\n"
        
        doc_section = "## 代码文档\n" + javadoc + "\n\n" if javadoc else ""
        structure_section = self.generate_file_structure(java_path)
        
        return (
            f"{header}"
            f"**文件路径**: `{java_path.relative_to(java_path.parents[2])}`\n\n"
            f"{doc_section}"
            f"{structure_section}"
            f"## 完整代码\n{code_block}"
        )

    @staticmethod
    def extract_package(code: str) -> Optional[str]:
        """使用正则表达式提取包声明"""
        package_match = re.search(r'^package\s+([\w.]+);', code, re.MULTILINE)
        return package_match.group(1) if package_match else ''

    @staticmethod
    def extract_javadoc(code: str) -> str:
        """提取Javadoc注释并转换为Markdown"""
        javadocs = re.findall(r'/\*\*.*?\*/', code, re.DOTALL)
        clean_docs = []
        for doc in javadocs:
            # 移除星号并清理格式
            cleaned = re.sub(r'^\s*\* ?', '', doc[3:-2], flags=re.MULTILINE)
            clean_docs.append(f"///\n{cleaned}\n///\n")
        return "\n".join(clean_docs)

    @staticmethod
    def generate_file_structure(java_path: Path) -> str:
        """生成文件结构说明"""
        rel_path = java_path.relative_to(java_path.parents[2])
        return (
            "## 文件结构\n"
            f"```\n"
            f"项目根目录\n"
            f"└── {rel_path.parent}\n"
            f"    └── {rel_path.name}\n"
            f"```\n\n"
        )

    def save_markdown(self, content: str, output_folder: str):
        """保存Markdown文件"""
        parts = Path(output_folder).parts
        # 获取项目名
        output_path = self.output_dir/Path(parts[len(parts)-1]+'.md')
        output_path.parent.mkdir(parents=True, exist_ok=True)
        
        with open(output_path, 'w', encoding='utf-8') as f:
            f.write(content)
        logging.info(f"生成文件: {output_path}")


if __name__ == "__main__":
    # 使用示例
    converter = JavaToMdConverter(
        output_dir="./docs",  # 输出目录
    )
    
    # 转换整个Java项目目录
    parent='gruul-global'
    children = [
        'gruul-global-model',
        'gruul-global-config',
        'gruul-global-i18n',
    ]
    if children:
        for child in children:
             converter.process_directory(f"./{parent}/{child}")
    else:
        converter.process_directory(f"./{parent}")