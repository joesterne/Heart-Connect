import re

with open('README.md', 'r') as f:
    content = f.read()

new_section = """
## ⚡ Performance & Security Optimizations
* **Bulk Database Transactions**: Optimized SQLite `INSERT` statements in `DailyLogDao` and `LogRepository` to use batch operations (`insertLogs`), drastically reducing database I/O latency during backup restorations.
* **Release Build Shrinking (R8)**: Enabled R8 minification (`isMinifyEnabled = true`) and resource shrinking in the Gradle release configuration to reduce APK size and obfuscate code against reverse engineering.
* **Asynchronous Cryptography**: Refactored `SecureStorageRepository` to execute AES-GCM file encryption and decryption on background threads (`Dispatchers.IO`) using Kotlin Coroutines, preventing Main Thread blocking and potential ANRs.
"""

# Insert after the journaling section or at the top after the intro
insert_pos = content.find("## 📓 Enhanced Daily Journaling")
if insert_pos != -1:
    content = content[:insert_pos] + new_section.strip() + "\n\n" + content[insert_pos:]
else:
    # fallback
    content += "\n" + new_section

with open('README.md', 'w') as f:
    f.write(content)

