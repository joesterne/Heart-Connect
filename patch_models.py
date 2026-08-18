import re
with open('app/src/main/java/com/example/data/model/Models.kt', 'r') as f:
    content = f.read()

content = content.replace('package com.example.data.model', 'package com.example.data.model\n\nimport androidx.compose.runtime.Immutable\n')
content = re.sub(r'(data class )', r'@Immutable\n\1', content)

with open('app/src/main/java/com/example/data/model/Models.kt', 'w') as f:
    f.write(content)
