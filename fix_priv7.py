with open("app/src/main/java/com/example/ui/screens/PrivateChatScreen.kt", "r") as f:
    text = f.read()

text = text.rstrip()
while text.endswith("}"):
    text = text[:-1].rstrip()

c = 0
for ch in text:
    if ch == '{': c += 1
    elif ch == '}': c -= 1

print("Total remaining to append:", c)
text = text + "\n" + "}\n" * c

with open("app/src/main/java/com/example/ui/screens/PrivateChatScreen.kt", "w") as f:
    f.write(text)
