module.exports = {
    extends: ['@commitlint/config-conventional'],
    rules: {
        'type-enum': [
            2,
            'always',
            [
                'feat', // 新功能（feature）
                'fix', // 修补bug
                'perf', //性能优化
                'docs', // 文档（documentation）
                'style', // 格式（不影响代码运行的变动）
                'refactor', // 重构（即不是新增功能，也不是修改bug的代码变动）
                'test', // 增加测试
                'revert', // 回滚
                'config', // 构建过程或辅助工具的变动
                'chore', // 其他改动
                'build', //构建配置修改
                'ci', //CI配置修改
                'Chores', //其他更新
                'merge',
            ],
        ],
        'subject-full-stop': [0, 'never'],
        'subject-case': [0, 'never'],
    },
}
