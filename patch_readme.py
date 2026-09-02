import re

with open('README.md', 'r') as f:
    content = f.read()

new_section = """
## 📓 Enhanced Daily Journaling & Trend Tracking
* **Interactive Mood Logging**: Upgraded the daily tracking feature with an intuitive, emoji-based horizontal mood selector, making it more expressive and easier for users to log their emotional state.
* **Room Database Persistence**: Fully migrated local data storage for daily logs from `SharedPreferences` to a robust, persistent **Room Database**. Implemented complete data models (`@Entity`), Data Access Objects (`DailyLogDao`), and a Repository layer for reactive, offline-first data management.
* **Visual Trend Analytics**: Integrated the **Vico** data visualization library to generate dynamic, interactive line charts. Users can now visualize up to 30 days of their mood history at a glance on their profile, directly connected to their Room database.
"""

# Insert after the first paragraph
insert_pos = content.find("## 📅 Support Group Schedule")
if insert_pos != -1:
    content = content[:insert_pos] + new_section.strip() + "\n\n" + content[insert_pos:]
else:
    content += "\n" + new_section

with open('README.md', 'w') as f:
    f.write(content)

