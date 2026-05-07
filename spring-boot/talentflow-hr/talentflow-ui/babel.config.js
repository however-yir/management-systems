module.exports = {
    presets: [
        '@vue/app',
        [
            '@babel/preset-env', {
            modules: false
        }]
    ],
    plugins: [
        [
            "component",
            {
                "libraryName": "element-ui",
                "styleLibraryName": "theme-chalk"
            }
        ]
    ],
    env: {
        test: {
            presets: [
                [
                    '@babel/preset-env',
                    {
                        targets: {
                            node: 'current'
                        },
                        modules: 'commonjs'
                    }
                ]
            ]
        }
    }
}
