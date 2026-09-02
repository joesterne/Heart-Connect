import re
with open('app/build.gradle.kts', 'r') as f:
    content = f.read()

content = content.replace("isMinifyEnabled = false", "isMinifyEnabled = true\n      isShrinkResources = true")

with open('app/build.gradle.kts', 'w') as f:
    f.write(content)
