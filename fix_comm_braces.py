with open("app/src/main/java/com/example/ui/screens/CommunityScreen.kt", "r") as f:
    text = f.read()

import re
funcs = [
    "fun CommunityFeed",
    "fun PostCard",
    "fun GroupCard",
    "fun QAFeed",
    "fun QACard",
    "fun CentersMap",
    "fun ScheduleFeed",
    "fun ScheduledSessionCard"
]

for func in funcs:
    idx = text.find(func)
    if idx != -1:
        c = 0
        for ch in text[:idx]:
            if ch == '{': c += 1
            elif ch == '}': c -= 1
        print(f"Before {func}: {c}")

c = 0
for ch in text:
    if ch == '{': c += 1
    elif ch == '}': c -= 1
print("End of file:", c)

