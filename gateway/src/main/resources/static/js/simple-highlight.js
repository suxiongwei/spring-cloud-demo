/**
 * 轻量级代码高亮库 - 用于 YAML 和 Java
 */
(function () {
    'use strict';

    // CSS 样式
    const styles = `
        .hljs-comment { color: #6a737d; font-style: italic; }
        .hljs-keyword { color: #d73a49; font-weight: bold; }
        .hljs-string { color: #032f62; }
        .hljs-number { color: #005cc5; }
        .hljs-literal { color: #005cc5; }
        .hljs-attr { color: #6f42c1; font-weight: 600; }
        .hljs-meta { color: #d73a49; }
        .hljs-title { color: #6f42c1; }
    `;

    // 添加样式到页面
    const styleElement = document.createElement('style');
    styleElement.textContent = styles;
    document.head.appendChild(styleElement);

    // 创建安全的文本节点和元素
    function createHighlightedElement(code, language) {
        const container = document.createElement('div');

        if (language === 'yaml') {
            highlightYaml(code, container);
        } else if (language === 'java') {
            highlightJava(code, container);
        } else {
            container.textContent = code;
        }

        return container.innerHTML;
    }

    // YAML 高亮
    function highlightYaml(code, container) {
        const lines = code.split('\n');

        lines.forEach((line, index) => {
            if (index > 0) {
                container.appendChild(document.createTextNode('\n'));
            }

            // 注释
            if (line.match(/^\s*#/)) {
                const span = document.createElement('span');
                span.className = 'hljs-comment';
                span.textContent = line;
                container.appendChild(span);
                return;
            }

            // 解析键值对
            const keyMatch = line.match(/^(\s*)([a-zA-Z0-9_-]+)(\s*:)(.*)$/);
            if (keyMatch) {
                container.appendChild(document.createTextNode(keyMatch[1])); // 缩进

                const keySpan = document.createElement('span');
                keySpan.className = 'hljs-attr';
                keySpan.textContent = keyMatch[2];
                container.appendChild(keySpan);

                container.appendChild(document.createTextNode(keyMatch[3])); // 冒号

                const value = keyMatch[4];
                if (value) {
                    // 检查值的类型
                    const trimmedValue = value.trim();
                    if (/^\d+$/.test(trimmedValue)) {
                        const numSpan = document.createElement('span');
                        numSpan.className = 'hljs-number';
                        numSpan.textContent = value;
                        container.appendChild(numSpan);
                    } else if (/^(true|false|null)$/.test(trimmedValue)) {
                        const litSpan = document.createElement('span');
                        litSpan.className = 'hljs-literal';
                        litSpan.textContent = value;
                        container.appendChild(litSpan);
                    } else if (trimmedValue.startsWith('#')) {
                        const commentSpan = document.createElement('span');
                        commentSpan.className = 'hljs-comment';
                        commentSpan.textContent = value;
                        container.appendChild(commentSpan);
                    } else {
                        container.appendChild(document.createTextNode(value));
                    }
                }
            } else {
                container.appendChild(document.createTextNode(line));
            }
        });
    }

    // Java 高亮
    function highlightJava(code, container) {
        const keywords = new Set(['public', 'private', 'protected', 'static', 'final', 'class',
            'interface', 'extends', 'implements', 'return', 'new', 'void', 'int', 'String',
            'boolean', 'if', 'else', 'for', 'while', 'try', 'catch', 'throw', 'throws']);

        const lines = code.split('\n');

        lines.forEach((line, lineIndex) => {
            if (lineIndex > 0) {
                container.appendChild(document.createTextNode('\n'));
            }

            // 单行注释
            if (line.trim().startsWith('//')) {
                const span = document.createElement('span');
                span.className = 'hljs-comment';
                span.textContent = line;
                container.appendChild(span);
                return;
            }

            // 分词处理
            let pos = 0;
            const tokens = [];
            const regex = /(@[A-Za-z]+|"[^"]*"|[a-zA-Z_][a-zA-Z0-9_]*|[0-9]+|[<>(){}[\];,.:+\-*/=]|\s+)/g;
            let match;

            while ((match = regex.exec(line)) !== null) {
                if (match.index > pos) {
                    tokens.push({ type: 'text', value: line.substring(pos, match.index) });
                }
                tokens.push({ type: 'token', value: match[0] });
                pos = match.index + match[0].length;
            }

            if (pos < line.length) {
                tokens.push({ type: 'text', value: line.substring(pos) });
            }

            // 渲染 tokens
            tokens.forEach(token => {
                const value = token.value;

                if (value.startsWith('@')) {
                    // 注解
                    const span = document.createElement('span');
                    span.className = 'hljs-meta';
                    span.textContent = value;
                    container.appendChild(span);
                } else if (value.startsWith('"')) {
                    // 字符串
                    const span = document.createElement('span');
                    span.className = 'hljs-string';
                    span.textContent = value;
                    container.appendChild(span);
                } else if (keywords.has(value)) {
                    // 关键字
                    const span = document.createElement('span');
                    span.className = 'hljs-keyword';
                    span.textContent = value;
                    container.appendChild(span);
                } else if (/^[A-Z][a-zA-Z0-9]*$/.test(value)) {
                    // 类名
                    const span = document.createElement('span');
                    span.className = 'hljs-title';
                    span.textContent = value;
                    container.appendChild(span);
                } else if (/^\d+$/.test(value)) {
                    // 数字
                    const span = document.createElement('span');
                    span.className = 'hljs-number';
                    span.textContent = value;
                    container.appendChild(span);
                } else {
                    // 普通文本
                    container.appendChild(document.createTextNode(value));
                }
            });
        });
    }

    // 自动高亮所有代码块
    function highlightAll() {
        document.querySelectorAll('.code-block pre code').forEach(block => {
            const language = block.className.match(/language-(\w+)/);
            if (language && language[1]) {
                const lang = language[1];
                const code = block.textContent;
                block.innerHTML = '';
                block.innerHTML = createHighlightedElement(code, lang);
                block.classList.add('hljs');
            }
        });
    }

    // 导出到全局
    window.simpleHighlight = {
        highlightAll: highlightAll,
        highlightCode: createHighlightedElement
    };

    // 自动执行
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', highlightAll);
    } else {
        highlightAll();
    }
})();
