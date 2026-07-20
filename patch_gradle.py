with open("app/build.gradle.kts", "r") as f:
    lines = f.readlines()

for i, line in enumerate(lines):
    if "dependencies {" in line:
        lines.insert(i + 1, '    implementation(libs.vico.compose)\n    implementation(libs.vico.compose.m3)\n')
        break

with open("app/build.gradle.kts", "w") as f:
    f.writelines(lines)
