import re

with open("gradle/libs.versions.toml", "r") as f:
    content = f.read()

content = content.replace('[versions]', '[versions]\nvico = "1.13.1"')

vico_libs = """
vico-compose = { group = "com.patrykandpatrick.vico", name = "compose", version.ref = "vico" }
vico-compose-m3 = { group = "com.patrykandpatrick.vico", name = "compose-m3", version.ref = "vico" }
"""

content = content.replace('[libraries]', '[libraries]' + vico_libs)

with open("gradle/libs.versions.toml", "w") as f:
    f.write(content)
